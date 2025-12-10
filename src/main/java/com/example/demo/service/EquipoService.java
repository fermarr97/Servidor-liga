package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.model.EquipoDTO;

@Service
public class EquipoService {
    private List<EquipoDTO> listaEquipos = new ArrayList<>();

    // lista en memoria
    public EquipoService() {
        listaEquipos.add(new EquipoDTO("Manzanares FC", "Juan López"));
        listaEquipos.add(new EquipoDTO("Membrilla CF", "Antonio García"));
        listaEquipos.add(new EquipoDTO("La Solana", "Pedro Martínez"));
        listaEquipos.add(new EquipoDTO("Valdepeñas", "Luis Sánchez"));
    }

    public List<EquipoDTO> obtenerTodos() { return listaEquipos; }
    
    // devuelve la lista ordenada por puntos de + a -
    public List<EquipoDTO> obtenerClasificacionOrdenada() {
        List<EquipoDTO> ordenada = new ArrayList<>(listaEquipos);
        
        // Ordenamos de Mayor a Menor Puntos
        Collections.sort(ordenada, new Comparator<EquipoDTO>() {
            @Override
            public int compare(EquipoDTO e1, EquipoDTO e2) {
                return e2.getPuntos() - e1.getPuntos();
            }
        });
        return ordenada;
    }

    // Añade un nuevo equipo
    public void guardarEquipo(EquipoDTO e) { listaEquipos.add(e); }

    // Busca y devuelve un equipo por nombre
    public EquipoDTO buscarPorNombre(String nombre) {
        for (int i = 0; i < listaEquipos.size(); i++) {
            if (listaEquipos.get(i).getNombre().equals(nombre)) return listaEquipos.get(i);
        }
        return null;
    }
}