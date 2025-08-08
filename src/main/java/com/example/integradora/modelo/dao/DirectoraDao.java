package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.*;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DirectoraDao {

    public static List<Resguardo> readDirectoraPorEstado(int estado) {
        List<Resguardo> lista = new ArrayList<>();
        String query = "SELECT " +
                "r.ID_RESGUARDO, r.FECHA, r.ESTADO AS estado_resguardo, " +
                "es.ID_ESPACIO, es.NOMBRE AS nombre_espacio, es.ESTADO AS estado_espacio, " +
                "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio, " +
                "em.RFC, em.NOMBRE AS nombre_empleado, em.APELLIDO_PATERNO, em.APELLIDO_MATERNO, em.ESTADO AS estado_empleado, " +
                "pu.ID_PUESTO, pu.NOMBRE AS nombre_puesto, pu.ESTADO AS estado_puesto, " +
                "ua.ID_UNIDAD, ua.NOMBRE AS nombre_unidad, ua.ESTADO AS estado_unidad " +
                "FROM RESGUARDO r " +
                "JOIN ESPACIO es ON r.ESPACIO_ID = es.ID_ESPACIO " +
                "JOIN EDIFICIO ed ON es.ID_EDIFICIO = ed.ID_EDIFICIO " +
                "JOIN EMPLEADO em ON r.RFC_EMPLEADO = em.RFC " +
                "JOIN PUESTO pu ON em.ID_PUESTO = pu.ID_PUESTO " +
                "JOIN UNIDAD_ADMINISTRATIVA ua ON em.ID_UNIDAD = ua.ID_UNIDAD " +
                "WHERE r.ESTADO = ? " +
                "ORDER BY r.ID_RESGUARDO ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Edificio edificio = new Edificio();
                    edificio.setId(rs.getInt("ID_EDIFICIO"));
                    edificio.setNombre(rs.getString("nombre_edificio"));
                    edificio.setEstado(rs.getInt("estado_edificio"));

                    Espacio espacio = new Espacio();
                    espacio.setId(rs.getInt("ID_ESPACIO"));
                    espacio.setNombre(rs.getString("nombre_espacio"));
                    espacio.setEstado(rs.getInt("estado_espacio"));
                    espacio.setEdificio(edificio);

                    Puesto puesto = new Puesto();
                    puesto.setId(rs.getInt("ID_PUESTO"));
                    puesto.setNombre(rs.getString("nombre_puesto"));
                    puesto.setEstado(rs.getInt("estado_puesto"));

                    UnidadAdministrativa unidad = new UnidadAdministrativa();
                    unidad.setId(rs.getInt("ID_UNIDAD"));
                    unidad.setNombre(rs.getString("nombre_unidad"));
                    unidad.setEstado(rs.getInt("estado_unidad"));

                    Empleado empleado = new Empleado();
                    empleado.setRfc(rs.getString("RFC"));
                    empleado.setNombre(rs.getString("nombre_empleado"));
                    empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                    empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                    empleado.setEstado(rs.getInt("estado_empleado"));
                    empleado.setPuesto(puesto);
                    empleado.setUnidadAdministrativa(unidad);

                    Resguardo resguardo = new Resguardo();
                    resguardo.setId(rs.getInt("ID_RESGUARDO"));
                    resguardo.setFecha(rs.getDate("FECHA").toLocalDate());
                    resguardo.setEstado(rs.getInt("estado_resguardo"));
                    resguardo.setEspacio(espacio);
                    resguardo.setEmpleado(empleado);
                    lista.add(resguardo);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;


    }
}


