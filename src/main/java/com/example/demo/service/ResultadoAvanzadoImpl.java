package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.model.JugadorDTO;
import com.example.demo.model.PartidoDTO;

@Service
@Qualifier("avanzado") 
public class ResultadoAvanzadoImpl extends ResultadoNormalImpl {

    // servicio de jugadores para buscarlos y actualizarlos
    @Autowired
    private JugadorService jugadorService;


    //Procesa tanto los puntos del equipo como las estadísticas de los jugadores

    @Override
    public void registrarResultado(PartidoDTO p, int gL, int gV, 
                                   Map<String, Integer> mapaGolesLocal, 
                                   Map<String, Integer> mapaGolesVisitante) {
        
        // Llamada al padre (super), calcular puntos, victorias y goles generales del equipo
        super.registrarResultado(p, gL, gV, mapaGolesLocal, mapaGolesVisitante);
        
        // ASIGNA GOLES A JUGADORES (Delanteros, Medios, Defensas)
        
        // --- Procesar Equipo Local ---
        if (mapaGolesLocal != null) {
            List<String> dnisLocal = new ArrayList<>(mapaGolesLocal.keySet());
            
            for (int i = 0; i < dnisLocal.size(); i++) {
                String dni = dnisLocal.get(i);
                // Recupera el número de goles asociados a ese DNI
                Integer golesEnPartido = mapaGolesLocal.get(dni);

                // Si el jugador marcó goles (> 0), actualizamos su ficha
                if (golesEnPartido != null && golesEnPartido > 0) {
                    JugadorDTO j = jugadorService.buscarPorDni(dni);
                    if (j != null) {
                        // Suma los goles de este partido al total histórico del jugador
                        j.setGoles(j.getGoles() + golesEnPartido);
                    }
                }
            }
        }

        // --- Procesar Equipo Visitante ---
        if (mapaGolesVisitante != null) {
            List<String> dnisVisitante = new ArrayList<>(mapaGolesVisitante.keySet());
            
            for (int i = 0; i < dnisVisitante.size(); i++) {
                String dni = dnisVisitante.get(i);
                Integer golesEnPartido = mapaGolesVisitante.get(dni);

                if (golesEnPartido != null && golesEnPartido > 0) {
                    JugadorDTO j = jugadorService.buscarPorDni(dni);
                    if (j != null) {
                        j.setGoles(j.getGoles() + golesEnPartido);
                    }
                }
            }
        }

        // ASIGNA GOLES EN CONTRA (Solo Porteros)
        // el portero recibe todos los goles que encajó su equipo.
        
        // --- Portero Local ---
        // Recibe todos los goles marcados por el equipo visitante (gV)
        List<JugadorDTO> plantillaLocal = p.getEquipoLocal().getJugadores();
        for(int i = 0; i < plantillaLocal.size(); i++) {
            JugadorDTO j = plantillaLocal.get(i);

            if(j.getPosicion().equalsIgnoreCase("Portero")) {
                j.setGolesEncajados(j.getGolesEncajados() + gV);
            }
        }

        // --- Portero Visitante ---
        // Recibe todos los goles marcados por el equipo local (gL)
        List<JugadorDTO> plantillaVisitante = p.getEquipoVisitante().getJugadores();
        for(int i = 0; i < plantillaVisitante.size(); i++) {
            JugadorDTO j = plantillaVisitante.get(i);
            if(j.getPosicion().equalsIgnoreCase("Portero")) {
                j.setGolesEncajados(j.getGolesEncajados() + gL);
            }
        }
    }
}