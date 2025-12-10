package com.example.demo.model;

public class PartidoDTO {
    private int id;
    private EquipoDTO equipoLocal;
    private EquipoDTO equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;

    public PartidoDTO(int id, EquipoDTO local, EquipoDTO visitante) {
        this.id = id;
        this.equipoLocal = local;
        this.equipoVisitante = visitante;
        this.jugado = false;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public EquipoDTO getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(EquipoDTO equipoLocal) { this.equipoLocal = equipoLocal; }
    public EquipoDTO getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(EquipoDTO equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    public int getGolesLocal() { return golesLocal; }
    public void setGolesLocal(int golesLocal) { this.golesLocal = golesLocal; }
    public int getGolesVisitante() { return golesVisitante; }
    public void setGolesVisitante(int golesVisitante) { this.golesVisitante = golesVisitante; }
    public boolean isJugado() { return jugado; }
    public void setJugado(boolean jugado) { this.jugado = jugado; }
}