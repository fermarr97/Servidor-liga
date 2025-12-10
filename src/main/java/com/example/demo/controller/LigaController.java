package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.EquipoDTO;
import com.example.demo.model.JugadorDTO;
import com.example.demo.model.PartidoDTO;
import com.example.demo.service.*;

@Controller
public class LigaController {

    @Autowired private EquipoService equipoService;
    @Autowired private JugadorService jugadorService;
    @Autowired private PartidoService partidoService;

    @Autowired
    @Qualifier("avanzado")
    private ResultadoService resultadoService;

    // --- INICIO ---
    @GetMapping("/") public String index() { return "index"; }

    // --- EQUIPOS ---
    // Muestra la lista de equipos
    @GetMapping("/equipos")
    public String verEquipos(Model model) {
        model.addAttribute("listaEquipos", equipoService.obtenerTodos());
        return "equipos/equipos_lista";
    }

    // formulario para crear un nuevo equipo
    @GetMapping("/equipos/nuevo") public String formEquipo() { return "equipos/equipos_alta"; }
    
    // comprueba el nuevo equipo
    @PostMapping("/equipos/guardar")
    public String guardarEquipo(@RequestParam String nombre, @RequestParam String entrenador, RedirectAttributes redirectAttrs) {
        if(equipoService.buscarPorNombre(nombre) == null) {
            equipoService.guardarEquipo(new EquipoDTO(nombre, entrenador));
            redirectAttrs.addFlashAttribute("mensaje", "Equipo creado.");
        } else {
            redirectAttrs.addFlashAttribute("error", "Error: Ya existe.");
        }
        return "redirect:/equipos";
    }

    // lista de jugadores
    @GetMapping("/jugadores")
    public String verJugadores(Model model) {
        model.addAttribute("listaJugadores", jugadorService.obtenerTodos());
        return "jugadores/jugadores_lista";
    }

    // formulario para un nuevo jugador, añade la lista de equipos
    @GetMapping("/jugadores/nuevo")
    public String formJugador(Model model) {
        model.addAttribute("listaEquipos", equipoService.obtenerTodos());
        return "jugadores/jugadores_alta";
    }

    // valida edad, asigna jugador al equipo y guarda
    @PostMapping("/jugadores/guardar")
    public String guardarJugador(JugadorDTO jugador, @RequestParam String nombreEquipo, RedirectAttributes redirectAttrs) {
        if(jugador.esMayorDe16()) {
            EquipoDTO e = equipoService.buscarPorNombre(nombreEquipo);
            if(e != null) {
                // Asignamos el nombre del equipo al jugador para las estadísticas
                jugador.setNombreEquipo(nombreEquipo);
                jugadorService.guardarJugador(jugador);
                e.agregarJugador(jugador);
                redirectAttrs.addFlashAttribute("mensaje", "Jugador fichado.");
                return "redirect:/jugadores";
            } else {
                redirectAttrs.addFlashAttribute("error", "Error: Equipo no encontrado.");
                return "redirect:/jugadores/nuevo";
            }
        } else {
            redirectAttrs.addFlashAttribute("error", "Error: Debe ser mayor de 16 años.");
            return "redirect:/jugadores/nuevo";
        }
    }
    
    // --- FICHA INDIVIDUAL JUGADOR ---
    // Muestra la ficha individual de un jugador buscado por DNI
    @GetMapping("/jugadores/detalle/{dni}")
    public String detalleJugador(@PathVariable String dni, Model model) {
        JugadorDTO j = jugadorService.buscarPorDni(dni);
        if(j != null) {
            model.addAttribute("jugador", j);
            return "jugadores/jugadores_detalle";
        }
        return "redirect:/jugadores";
    }

    // --- PARTIDOS ---
    // Muestra la lista de partidos
    @GetMapping("/partidos")
    public String verPartidos(Model model) {
        model.addAttribute("partidos", partidoService.obtenerTodos());
        return "partidos/partidos_lista";
    }

    // formulario para programar un partido, añade la lista de equipos
    @GetMapping("/partidos/nuevo")
    public String formPartido(Model model) {
        model.addAttribute("equipos", equipoService.obtenerTodos());
        return "partidos/partidos_alta";
    }

    // comprueba que son equipos distintos y crea el partido
    @PostMapping("/partidos/guardar")
    public String guardarPartido(@RequestParam String local, @RequestParam String visitante, RedirectAttributes redirectAttrs) {
        if(local.equals(visitante)) {
             redirectAttrs.addFlashAttribute("error", "Error: Mismo equipo.");
             return "redirect:/partidos/nuevo";
        }
        EquipoDTO e1 = equipoService.buscarPorNombre(local);
        EquipoDTO e2 = equipoService.buscarPorNombre(visitante);
        if(e1 != null && e2 != null) {
            partidoService.crearPartido(e1, e2);
            redirectAttrs.addFlashAttribute("mensaje", "Partido programado.");
            return "redirect:/partidos";
        }
        return "redirect:/partidos/nuevo";
    }

    // --- RESULTADOS ---
    // formulario para registrar el resultado si el partido existe y no está jugado
    @GetMapping("/partidos/jugar/{id}")
    public String formResultado(@PathVariable int id, Model model, RedirectAttributes red) {
        PartidoDTO p = partidoService.buscarPorId(id);
        if(p == null || p.isJugado()) {
            red.addFlashAttribute("error", "Error: No disponible.");
            return "redirect:/partidos";
        }
        model.addAttribute("partido", p);
        return "partidos/partidos_resultado";
    }

    // guarda el resultado y calcula goles a partir de las listas de goleadores
    @PostMapping("/partidos/registrar-resultado")
    public String registrarResultado(@RequestParam int idPartido, 
                                     // listas de IDs, checkbox de quienes marcaron
                                     @RequestParam(required = false) List<String> goleadoresLocal, 
                                     @RequestParam(required = false) List<String> goleadoresVisitante,
                                     RedirectAttributes redirectAttrs) {
        PartidoDTO p = partidoService.buscarPorId(idPartido);
        if(p != null && !p.isJugado()) {
            // goles segun cuantos checkbox se han marcado
            int golesL = (goleadoresLocal != null) ? goleadoresLocal.size() : 0;
            int golesV = (goleadoresVisitante != null) ? goleadoresVisitante.size() : 0;

            // Llamamos al servicio con las listas
            resultadoService.registrarResultado(p, golesL, golesV, goleadoresLocal, goleadoresVisitante);
            
            redirectAttrs.addFlashAttribute("mensaje", "Resultado y goleadores registrados.");
        }
        return "redirect:/clasificacion";
    }

    // --- CLASIFICACIÓN ---
    // Muestra la clasificación ordenada por puntos
    @GetMapping("/clasificacion")
    public String verClasificacion(Model model) {
        // Usamos el método que devuelve ordenado
        model.addAttribute("equipos", equipoService.obtenerClasificacionOrdenada());
        return "clasificacion";
    }

    // --- ESTADÍSTICAS ---
    // Muestra las estadísticas: goleadores y porteros menos goleados
    @GetMapping("/estadisticas")
    public String verEstadisticas(Model model) {
        model.addAttribute("goleadores", jugadorService.obtenerGoleadoresOrdenados());
        model.addAttribute("porteros", jugadorService.obtenerPorterosMenosGoleados());
        return "estadisticas"; 
    }
}