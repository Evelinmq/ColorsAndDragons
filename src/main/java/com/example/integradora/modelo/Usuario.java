package com.example.integradora.modelo;

public class Usuario {

    private String correo;
    private String contrasena;
    private String rfcEmpleado;
    private int estado;
    private String Rol;

    public Usuario(String correo, String contrasena, String rfcEmpleado, int estado, String Rol) {
        this.correo = correo;
        this.contrasena = contrasena;
        this.rfcEmpleado = rfcEmpleado;
        this.estado = estado;
        this.Rol = Rol;
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getRol() {
        return Rol;
    }

    public void setRol(String rol) {
        Rol = rol;
    }
}
