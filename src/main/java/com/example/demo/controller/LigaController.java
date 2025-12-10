package com.example.demo.controller;

import java.util.*;
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
    // Muestra la página principal
    @GetMapping("/") public String index() { return "index"; }

    // --- EQUIPOS ---
    // Muestra la lista de equipos
    @GetMapping("/equipos")
    public String verEquipos(Model model) {
        model.addAttribute("listaEquipos", equipoService.obtenerTodos());
        return "equipos/equipos_lista";
    }

    // Muestra el formulario para crear un nuevo equipo
    @GetMapping("/equipos/nuevo") public String formEquipo() { return "equipos/equipos_alta"; }
    
    // Guarda un equipo nuevo si no existe otro con el mismo nombre
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

    // --- JUGADORES ---
    // Muestra la lista de jugadores
    @GetMapping("/jugadores")
    public String verJugadores(Model model) {
        model.addAttribute("listaJugadores", jugadorService.obtenerTodos());
        return "jugadores/jugadores_lista";
    }

    // Muestra el formulario para crear un nuevo jugador y los equipos disponibles
    @GetMapping("/jugadores/nuevo")
    public String formJugador(Model model) {
        model.addAttribute("listaEquipos", equipoService.obtenerTodos());
        return "jugadores/jugadores_alta";
    }

    // Guarda un jugador si es mayor de 16 y el equipo existe
    @PostMapping("/jugadores/guardar")
    public String guardarJugador(JugadorDTO jugador, @RequestParam String nombreEquipo, RedirectAttributes redirectAttrs) {
        if(jugador.esMayorDe16()) {
            EquipoDTO e = equipoService.buscarPorNombre(nombreEquipo);
            if(e != null) {
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
    
    // Muestra el detalle de un jugador buscándolo por DNI
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

    // Muestra el formulario para programar un partido con los equipos disponibles
    @GetMapping("/partidos/nuevo")
    public String formPartido(Model model) {
        model.addAttribute("equipos", equipoService.obtenerTodos());
        return "partidos/partidos_alta";
    }

    // Crea un partido si los equipos existen y no son el mismo
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
    // Muestra el formulario para introducir el resultado de un partido no jugado
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

    // Procesa y registra el resultado del partido, incluyendo goles por jugador
    @PostMapping("/partidos/registrar-resultado")
    public String registrarResultado(@RequestParam int idPartido, 
                                     @RequestParam Map<String, String> allParams,
                                     RedirectAttributes redirectAttrs) {
        
        PartidoDTO p = partidoService.buscarPorId(idPartido);
        
        if(p != null && !p.isJugado()) {
            
            // Mapas limpios donde se guarda <DNI_REAL, GOLES>
            Map<String, Integer> mapaLocalLimpio = new HashMap<>();
            Map<String, Integer> mapaVisitanteLimpio = new HashMap<>();
            
            int totalGolesLocal = 0;
            int totalGolesVisitante = 0;

            // Recorremos TODOS los parámetros que llegaron del formulario
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                String key = entry.getKey();   // Ej: "golesLocal[12345678Z]"
                String value = entry.getValue(); // Ej: "2"

                try {
                    int goles = Integer.parseInt(value);

                    // Filtrar goles LOCALES
                    if (key.startsWith("golesLocal[")) {
                        // se saca el DNI que está entre los corchetes
                        // "golesLocal[".length() es 11. 
                        // Quitamos el último carácter "]"
                        String dniReal = key.substring(11, key.length() - 1);
                        
                        mapaLocalLimpio.put(dniReal, goles);
                        totalGolesLocal += goles;
                    } 
                    // Filtrar goles VISITANTES
                    else if (key.startsWith("golesVisitante[")) {
                        // "golesVisitante[".length() es 15
                        String dniReal = key.substring(15, key.length() - 1);
                        
                        mapaVisitanteLimpio.put(dniReal, goles);
                        totalGolesVisitante += goles;
                    }
                    
                } catch (NumberFormatException e) {

                } catch (StringIndexOutOfBoundsException e) {

                }
            }

            // datos ya limpios y separados correctamente
            resultadoService.registrarResultado(p, totalGolesLocal, totalGolesVisitante, mapaLocalLimpio, mapaVisitanteLimpio);
            
            redirectAttrs.addFlashAttribute("mensaje", "Resultado registrado: " + totalGolesLocal + " - " + totalGolesVisitante);
        }
        return "redirect:/clasificacion";
    }

    // --- CLASIFICACIÓN Y ESTADÍSTICAS ---
    // Muestra la clasificación ordenada de equipos
    @GetMapping("/clasificacion")
    public String verClasificacion(Model model) {
        model.addAttribute("equipos", equipoService.obtenerClasificacionOrdenada());
        return "clasificacion";
    }

    // Muestra estadísticas de goleadores y porteros menos goleados
    @GetMapping("/estadisticas")
    public String verEstadisticas(Model model) {
        model.addAttribute("goleadores", jugadorService.obtenerGoleadoresOrdenados());
        model.addAttribute("porteros", jugadorService.obtenerPorterosMenosGoleados());
        return "estadisticas"; 
    }
}