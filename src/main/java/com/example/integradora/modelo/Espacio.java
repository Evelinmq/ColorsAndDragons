package com.example.integradora.modelo;

public class Espacio {

    private int id;
    private String nombre;
    private int idEdificio;
    private int estado;


    public Espacio(int id, String nombre, int idEdificio, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.idEdificio = idEdificio;
        this.estado = estado;
    }

    public Espacio() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
