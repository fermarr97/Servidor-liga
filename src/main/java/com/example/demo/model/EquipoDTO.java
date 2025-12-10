package com.example.demo.model;

import java.util.ArrayList;
import java.util.List;

public class EquipoDTO {
    private String nombre;
    private String entrenador;
    private List<JugadorDTO> jugadores = new ArrayList<>();
    
    // Estadísticas
    private int victorias;
    private int derrotas;
    private int empates;
    private int golesFavor;
    private int golesContra;
    private int puntos;

    public EquipoDTO() {}

    public EquipoDTO(String nombre, String entrenador) {
        this.nombre = nombre;
        this.entrenador = entrenador;
    }

    public void agregarJugador(JugadorDTO j) {
        // Validación simple: máx 23 jugadores
        if(jugadores.size() < 23) {
            jugadores.add(j);
        }
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getEntrenador() { return entrenador; }
    public void setEntrenador(String entrenador) { this.entrenador = entrenador; }
    public List<JugadorDTO> getJugadores() { return jugadores; }
    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }
    public int getVictorias() { return victorias; }
    public void setVictorias(int victorias) { this.victorias = victorias; }
    public int getDerrotas() { return derrotas; }
    public void setDerrotas(int derrotas) { this.derrotas = derrotas; }
    public int getEmpates() { return empates; }
    public void setEmpates(int empates) { this.empates = empates; }
    public int getGolesFavor() { return golesFavor; }
    public void setGolesFavor(int gf) { this.golesFavor = gf; }
    public int getGolesContra() { return golesContra; }
    public void setGolesContra(int gc) { this.golesContra = gc; }
}