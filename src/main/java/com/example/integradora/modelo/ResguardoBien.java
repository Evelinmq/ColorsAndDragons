package com.example.integradora.modelo;

public class ResguardoBien {
    private int id;
    private Resguardo resguardo;
    private Bien bien;
    private Espacio espacio;
    private Edificio edificio;
    private Empleado empleado;
    private UnidadAdministrativa unidad;
    private Puesto puesto;
    private int estado;

    public ResguardoBien() {
    }

    public ResguardoBien(int id, Resguardo resguardo, Bien bien, Espacio espacio, Edificio edificio, Empleado empleado, UnidadAdministrativa unidad, Puesto puesto, int estado) {
        this.id = id;
        this.resguardo = resguardo;
        this.bien = bien;
        this.espacio = espacio;
        this.edificio = edificio;
        this.empleado = empleado;
        this.unidad = unidad;
        this.puesto = puesto;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Resguardo getResguardo() {
        return resguardo;
    }

    public void setResguardo(Resguardo resguardo) {
        this.resguardo = resguardo;
    }

    public Bien getBien() {
        return bien;
    }

    public void setBien(Bien bien) {
        this.bien = bien;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }

    public Edificio getEdificio() {
        return edificio;
    }

    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public UnidadAdministrativa getUnidad() {
        return unidad;
    }

    public void setUnidad(UnidadAdministrativa unidad) {
        this.unidad = unidad;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }
}
