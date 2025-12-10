package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.example.demo.model.JugadorDTO;
import com.example.demo.model.PartidoDTO;
import java.util.List;

@Service
@Qualifier("avanzado") 
public class ResultadoAvanzadoImpl extends ResultadoNormalImpl {

    // servicio de jugadores para poder buscarlos por DNI y modificarlos
    @Autowired
    private JugadorService jugadorService;

    
    // Procesa el resultado final de un partido.
    @Override
    public void registrarResultado(PartidoDTO p, int gL, int gV, List<String> dnisLocal, List<String> dnisVisitante) {
        
        // asignar los puntos por victorias, empates y derrotas y goles totales al equipo
        super.registrarResultado(p, gL, gV, dnisLocal, dnisVisitante);
        
        // Procesar goleadores del Equipo Local
        if (dnisLocal != null) {
            for (int i = 0; i < dnisLocal.size(); i++) {
                String dni = dnisLocal.get(i);
                JugadorDTO j = jugadorService.buscarPorDni(dni);
                if (j != null) {
                    // Sumamos +1 al contador personal del jugador
                    j.setGoles(j.getGoles() + 1); 
                }
            }
        }

        // Procesar goleadores del Equipo Visitante
        if (dnisVisitante != null) {
            for (int i = 0; i < dnisVisitante.size(); i++) {
                String dni = dnisVisitante.get(i);
                JugadorDTO j = jugadorService.buscarPorDni(dni);
                if (j != null) {
                    j.setGoles(j.getGoles() + 1);
                }
            }
        }

        // Al portero local le sumamos los goles totales del visitante, y viceversa.
        // Actualizar Portero Local
        List<JugadorDTO> plantillaLocal = p.getEquipoLocal().getJugadores();
        for(int i = 0; i < plantillaLocal.size(); i++) {
            JugadorDTO j = plantillaLocal.get(i);
            if(j.getPosicion().equals("Portero")) {
                j.setGolesEncajados(j.getGolesEncajados() + gV);
            }
        }

        // Actualizar Portero Visitante
        List<JugadorDTO> plantillaVisitante = p.getEquipoVisitante().getJugadores();
        for(int i = 0; i < plantillaVisitante.size(); i++) {
            JugadorDTO j = plantillaVisitante.get(i);
            if(j.getPosicion().equals("Portero")) {
                j.setGolesEncajados(j.getGolesEncajados() + gL);
            }
        }
    }
}