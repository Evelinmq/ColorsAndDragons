package com.example.integradora.modelo;

public class Edificio {

    private int id;
    private String nombre;
    private int estado;

    public Edificio(int id, int estado, String nombre) {
        this.id = id;
        this.estado = estado;
        this.nombre = nombre;
    }
    public Edificio(String nombre) {
        this.nombre = nombre;
        this.estado = 1; // activo por defecto
    }

    public Edificio() {
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return nombre; // Muestra el nombre del edificio en el ComboBox
    }

}
