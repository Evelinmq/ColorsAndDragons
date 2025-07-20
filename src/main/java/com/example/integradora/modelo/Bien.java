package com.example.integradora.modelo;

public class Bien {

    private String bien_codigo;
    private String descripcion;
    private String marca;
    private String modelo;
    private String Serie;
    private int estado;

    public Bien(String bien_codigo, String descripcion, String marca, String modelo, String Serie, int estado) {
        this.bien_codigo = bien_codigo;
        this.descripcion = descripcion;
        this.marca = marca;
        this.modelo = modelo;
        this.Serie = Serie;
        this.estado = estado;
    }

    public Bien() {
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getBien_codigo() {
        return bien_codigo;
    }

    public void setBien_codigo(String bien_codigo) {
        this.bien_codigo = bien_codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getSerie() {
        return Serie;
    }

    public void setSerie(String Serie) {
        this.Serie = Serie;
    }

    @Override
    public String toString() {
        return bien_codigo;
    }
}
