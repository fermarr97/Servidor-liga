package com.example.demo.service;

import com.example.demo.model.PartidoDTO;
import java.util.Map;

public interface ResultadoService {
    
    // Registra el resultado de un partido
    
    void registrarResultado(PartidoDTO partido, int golesLocal, int golesVisitante, 
                            Map<String, Integer> mapaGolesLocal, 
                            Map<String, Integer> mapaGolesVisitante);
}