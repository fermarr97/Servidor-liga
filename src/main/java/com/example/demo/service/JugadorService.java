package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.demo.model.JugadorDTO;

@Service
public class JugadorService {
    private List<JugadorDTO> listaJugadores = new ArrayList<>();

    // Devuelve la lista completa de jugadores almacenada en memoria
    public List<JugadorDTO> obtenerTodos() { return listaJugadores; }

    // Añade un nuevo jugador a la lista
    public void guardarJugador(JugadorDTO j) { listaJugadores.add(j); }

    // Busca y devuelve un jugador por su DNI, null si no se encuentra
    public JugadorDTO buscarPorDni(String dni) {
        for(int i=0; i < listaJugadores.size(); i++) {
            if(listaJugadores.get(i).getDni().equals(dni)) return listaJugadores.get(i);
        }
        return null;
    }

    // Devuelve lista ordenada por goles de + a -
    public List<JugadorDTO> obtenerGoleadoresOrdenados() {
        // Copiamos la lista para no alterar la original
        List<JugadorDTO> copia = new ArrayList<>(listaJugadores);
        
        // ordenamos por goles DESC
        Collections.sort(copia, new Comparator<JugadorDTO>() {
            @Override
            public int compare(JugadorDTO j1, JugadorDTO j2) {
                return j2.getGoles() - j1.getGoles(); // De mayor a menor
            }
        });
        return copia;
    }

    // Devuelve lista de porteros ordenada por goles encajados de - a +
    public List<JugadorDTO> obtenerPorterosMenosGoleados() {
        List<JugadorDTO> porteros = new ArrayList<>();
        // Filtramos solo porteros
        for(int i=0; i<listaJugadores.size(); i++) {
            if(listaJugadores.get(i).getPosicion().equalsIgnoreCase("Portero")) {
                porteros.add(listaJugadores.get(i));
            }
        }
        // Ordenamos por goles encajados ASC
        Collections.sort(porteros, new Comparator<JugadorDTO>() {
            @Override
            public int compare(JugadorDTO j1, JugadorDTO j2) {
                return j1.getGolesEncajados() - j2.getGolesEncajados();
            }
        });
        return porteros;
    }
}