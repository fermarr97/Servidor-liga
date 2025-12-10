package com.example.demo.model;

public class JugadorDTO {
    private String nombre;
    private String dni;
    private int edad;
    private String posicion;
    private String nombreEquipo;
    private int goles;
    private int golesEncajados; //(solo porteros)
    
    public JugadorDTO() {}

    public JugadorDTO(String nombre, String dni, int edad, String posicion) {
        this.nombre = nombre;
        this.dni = dni;
        this.edad = edad;
        this.posicion = posicion;
        this.goles = 0;
        this.golesEncajados = 0;
    }

    public boolean esMayorDe16() {
        return this.edad > 16;
    }

    // Getters y Setters actualizados
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }
    public String getPosicion() { return posicion; }
    public void setPosicion(String posicion) { this.posicion = posicion; }
    public String getNombreEquipo() { return nombreEquipo; }
    public void setNombreEquipo(String nombreEquipo) { this.nombreEquipo = nombreEquipo; }
    public int getGoles() { return goles; }
    public void setGoles(int goles) { this.goles = goles; }
    public int getGolesEncajados() { return golesEncajados; }
    public void setGolesEncajados(int golesEncajados) { this.golesEncajados = golesEncajados; }
}