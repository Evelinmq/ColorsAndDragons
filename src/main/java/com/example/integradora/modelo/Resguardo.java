package com.example.integradora.modelo;



//No sé cuál de los dos import para la Fecha sea el correcto, así que puse los dos
import java.sql.Date;
//import java.util.Date;

public class Resguardo {
    private int id;
    private Date fecha;
    private int estado;
    private Empleado empleado;
    private Espacio espacio;

    public Resguardo() {
    }

    public Resguardo(int id, Date fecha, int estado, Empleado empleado, Espacio espacio) {
        this.id = id;
        this.fecha = fecha;
        this.estado = estado;
        this.empleado = empleado;
        this.espacio = espacio;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }
}
