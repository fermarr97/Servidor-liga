package com.example.demo.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import com.example.demo.model.EquipoDTO;
import com.example.demo.model.PartidoDTO;
import java.util.List;

@Service
@Primary
public class ResultadoNormalImpl implements ResultadoService {

    /* Implementación estándar de registro de resultados.
       Actualiza goles del partido, marca como jugado y ajusta estadísticas
       (goles a favor/contra, puntos, victorias/empates/derrotas) de los equipos. */
    @Override
    public void registrarResultado(PartidoDTO p, int gL, int gV, List<String> dnisLocal, List<String> dnisVisitante) {
        // Lógica estándar de puntos y goles totales del equipo
        p.setGolesLocal(gL);
        p.setGolesVisitante(gV);
        p.setJugado(true);

        EquipoDTO local = p.getEquipoLocal();
        EquipoDTO visit = p.getEquipoVisitante();

        local.setGolesFavor(local.getGolesFavor() + gL);
        local.setGolesContra(local.getGolesContra() + gV);
        visit.setGolesFavor(visit.getGolesFavor() + gV);
        visit.setGolesContra(visit.getGolesContra() + gL);

        if (gL > gV) {
            local.setPuntos(local.getPuntos() + 3);
            local.setVictorias(local.getVictorias() + 1);
            visit.setDerrotas(visit.getDerrotas() + 1);
        } else if (gV > gL) {
            visit.setPuntos(visit.getPuntos() + 3);
            visit.setVictorias(visit.getVictorias() + 1);
            local.setDerrotas(local.getDerrotas() + 1);
        } else {
            local.setPuntos(local.getPuntos() + 1);
            visit.setPuntos(visit.getPuntos() + 1);
            local.setEmpates(local.getEmpates() + 1);
            visit.setEmpates(visit.getEmpates() + 1);
        }
    }
}