package com.example.integradora.modelo;

public class Espacio {

    private int id;
    private String nombre;
    private Edificio edificio;
    private int estado;

    public Espacio(int id, String nombre, Edificio edificio, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.edificio = edificio;
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

    public Edificio getEdificio() {
        return edificio;
    }

    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}

