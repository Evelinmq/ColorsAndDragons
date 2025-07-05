package com.example.integradora.modelo;

public class Usuario {

    private String correo;
    private String contrasena;
    private String rfcEmpleado;
    private int idUnidad;
    private int idPuesto;
    private int estado;

    public Usuario(String correo, String contrasena, String rfcEmpleado, int idUnidad, int idPuesto, int estado) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rfcEmpleado = rfcEmpleado;
        this.idUnidad = idUnidad;
        this.idPuesto = idPuesto;
        this.estado = estado;
    }

    public Usuario() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRfcEmpleado() {
        return rfcEmpleado;
    }

    public void setRfcEmpleado(String rfcEmpleado) {
        this.rfcEmpleado = rfcEmpleado;
    }

    public int getIdUnidad() {
        return idUnidad;
    }

    public void setIdUnidad(int idUnidad) {
        this.idUnidad = idUnidad;
    }

    public int getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(int idPuesto) {
        this.idPuesto = idPuesto;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
