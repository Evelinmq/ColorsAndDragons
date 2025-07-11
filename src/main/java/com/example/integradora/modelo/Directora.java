package com.example.integradora.modelo;

public class Directora {

    private int idResguardo;
    private String codigoBien;
    private int idEspacio;
    private int idEdificio;
    private String rfcEmpleado;
    private int idUnidad;
    private int idPuesto;

    public Directora() {
    }

    public Directora(int idResguardo, String codigoBien, int idEspacio, int idEdificio, String rfcEmpleado, int idUnidad, int idPuesto) {
        this.idResguardo = idResguardo;
        this.codigoBien = codigoBien;
        this.idEspacio = idEspacio;
        this.idEdificio = idEdificio;
        this.rfcEmpleado = rfcEmpleado;
        this.idUnidad = idUnidad;
        this.idPuesto = idPuesto;
    }

    public int getIdResguardo() {
        return idResguardo;
    }

    public void setIdResguardo(int idResguardo) {
        this.idResguardo = idResguardo;
    }

    public String getCodigoBien() {
        return codigoBien;
    }

    public void setCodigoBien(String codigoBien) {
        this.codigoBien = codigoBien;
    }

    public int getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(int idEspacio) {
        this.idEspacio = idEspacio;
    }

    public int getIdEdificio() {
        return idEdificio;
    }

    public void setIdEdificio(int idEdificio) {
        this.idEdificio = idEdificio;
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
}
