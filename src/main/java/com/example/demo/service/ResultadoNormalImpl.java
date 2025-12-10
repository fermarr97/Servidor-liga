package com.example.demo.service;

import java.util.Map;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import com.example.demo.model.EquipoDTO;
import com.example.demo.model.PartidoDTO;

@Service
@Primary 
public class ResultadoNormalImpl implements ResultadoService {

    // Actualiza el estado del partido y recalcula la clasificación de los equipos
    
    @Override
    public void registrarResultado(PartidoDTO p, int gL, int gV, 
                                   Map<String, Integer> mapaLocal, 
                                   Map<String, Integer> mapaVisitante) {
        
        // Actualiza datos básicos del partido
        p.setGolesLocal(gL);
        p.setGolesVisitante(gV);
        p.setJugado(true);

        // consigue referencias a los objetos equipo
        EquipoDTO local = p.getEquipoLocal();
        EquipoDTO visit = p.getEquipoVisitante();

        // actualiz estadísticas de goles a favor y en contra totales
        local.setGolesFavor(local.getGolesFavor() + gL);
        local.setGolesContra(local.getGolesContra() + gV);
        visit.setGolesFavor(visit.getGolesFavor() + gV);
        visit.setGolesContra(visit.getGolesContra() + gL);

        // reparto de puntos (3 victoria, 1 empate, 0 derrota)
        if (gL > gV) {
            // Gana Local
            local.setPuntos(local.getPuntos() + 3);
            local.setVictorias(local.getVictorias() + 1);
            visit.setDerrotas(visit.getDerrotas() + 1);
        } else if (gV > gL) {
            // Gana Visitante
            visit.setPuntos(visit.getPuntos() + 3);
            visit.setVictorias(visit.getVictorias() + 1);
            local.setDerrotas(local.getDerrotas() + 1);
        } else {
            // Empate
            local.setPuntos(local.getPuntos() + 1);
            visit.setPuntos(visit.getPuntos() + 1);
            local.setEmpates(local.getEmpates() + 1);
            visit.setEmpates(visit.getEmpates() + 1);
        }
    }
}