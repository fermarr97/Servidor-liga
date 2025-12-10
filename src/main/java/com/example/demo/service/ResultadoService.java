package com.example.demo.service;

import com.example.demo.model.PartidoDTO;
import java.util.List;

public interface ResultadoService {
    /* Ahora el método acepta las listas de DNI de los jugadores que marcaron */
    void registrarResultado(PartidoDTO partido, int golesLocal, int golesVisitante, 
                            List<String> dnisGoleadoresLocal, List<String> dnisGoleadoresVisitante);
}