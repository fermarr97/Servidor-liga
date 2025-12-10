package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.model.EquipoDTO;
import com.example.demo.model.PartidoDTO;

@Service
public class PartidoService {

    // Lista en memoria para los partidos
    private List<PartidoDTO> listaPartidos = new ArrayList<>();
    // Contador simple para generar IDs únicos
    private int contadorIds = 1;

    // obtener todos los partidos 
    public List<PartidoDTO> obtenerTodos() {
        return listaPartidos;
    }

    // crear un nuevo partido y añadirlo a la lista
    public void crearPartido(EquipoDTO local, EquipoDTO visitante) {
        PartidoDTO nuevoPartido = new PartidoDTO(contadorIds, local, visitante);
        listaPartidos.add(nuevoPartido);
        contadorIds++;
    }

    //busca un partido por ID
    public PartidoDTO buscarPorId(int id) {
    	
        for (int i = 0; i < listaPartidos.size(); i++) {
            PartidoDTO p = listaPartidos.get(i);

            if (p.getId() == id) {
                return p; 
            }
        }
        return null;
    }
}