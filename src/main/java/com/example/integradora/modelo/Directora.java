package com.example.integradora.modelo;

import java.time.LocalDate;

public class Directora {

    private int idResguardo;
    private LocalDate fecha;
    private int estado;
    private Empleado empleado;
    private Espacio espacio;

    public Directora() {}

    public Directora(int idResguardo, LocalDate fecha, int estado, Empleado empleado, Espacio espacio) {
        this.idResguardo = idResguardo;
        this.fecha = fecha;
        this.estado = estado;
        this.empleado = empleado;
        this.espacio = espacio;
    }

    public int getIdResguardo() { return idResguardo; }
    public void setIdResguardo(int idResguardo) { this.idResguardo = idResguardo; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public int getEstado() { return estado; }
    public void setEstado(int estado) { this.estado = estado; }

    public Empleado getEmpleado() { return empleado; }
    public void setEmpleado(Empleado empleado) { this.empleado = empleado; }

    public Espacio getEspacio() { return espacio; }
    public void setEspacio(Espacio espacio) { this.espacio = espacio; }

    public String getNombreEmpleadoCompleto() {
        if (empleado == null) return "";
        String ap1 = empleado.getApellidoPaterno() == null ? "" : empleado.getApellidoPaterno();
        String ap2 = empleado.getApellidoMaterno() == null ? "" : empleado.getApellidoMaterno();
        String nom = empleado.getNombre() == null ? "" : empleado.getNombre();
        return (nom + " " + ap1 + " " + ap2).trim().replaceAll(" +", " ");
    }

    public String getRfcEmpleado() {
        return (empleado != null && empleado.getRfc() != null) ? empleado.getRfc() : "";
    }

    public String getNombreEspacio() {
        return (espacio != null && espacio.getNombre() != null) ? espacio.getNombre() : "";
    }

    public String getEstadoTexto() {
        return estado == 1 ? "Activo" : "Inactivo";
    }
}

