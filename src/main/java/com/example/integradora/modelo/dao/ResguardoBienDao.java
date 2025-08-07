package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.*;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResguardoBienDao {


    public boolean insertarResguardoBien(List<ResguardoBien> listaBienes) {
        String query = "INSERT INTO RESGUARDO_BIEN (RESBIEN_RESGUID, RESBIEN_CODBIEN, RESBIEN_ESPACIO, RESBIEN_EDIFI, RESBIEN_EMPLEADO, RESBIEN_UNIDAD, RESBIEN_PUESTO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            conn.setAutoCommit(false); // Iniciar una transacción

            for (ResguardoBien rb : listaBienes) {
                ps.setInt(1, rb.getResguardo().getId());
                ps.setString(2, rb.getBien().getBien_codigo());
                ps.setInt(3, rb.getEspacio().getId());
                ps.setInt(4, rb.getEdificio().getId());
                ps.setString(5, rb.getEmpleado().getRfc());
                ps.setInt(6, rb.getUnidad().getId());
                ps.setInt(7, rb.getPuesto().getId());
                ps.addBatch();
            }

            ps.executeBatch();
            conn.commit(); // Confirma la transacción
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateResguardoBien(ResguardoBien rb) {
        String query = "UPDATE RESGUARDO_BIEN SET " +
                "RESBIEN_RESGUID = ?, RESBIEN_CODBIEN = ?, RESBIEN_ESPACIO = ?, RESBIEN_EDIFI = ?, " +
                "RESBIEN_EMPLEADO = ?, RESBIEN_UNIDAD = ?, RESBIEN_PUESTO = ? " +
                "WHERE ID_RESGUARDOBIEN = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, rb.getResguardo().getId());
            ps.setString(2, rb.getBien().getBien_codigo());
            ps.setInt(3, rb.getEspacio().getId());
            ps.setInt(4, rb.getEdificio().getId());
            ps.setString(5, rb.getEmpleado().getRfc());
            ps.setInt(6, rb.getUnidad().getId());
            ps.setInt(7, rb.getPuesto().getId());
            ps.setInt(8, rb.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean deleteResguardoBien(String codigoBien, int idResguardo) {
        boolean exito = false;
        String query = "DELETE FROM RESGUARDO_BIEN WHERE RESBIEN_CODBIEN = ? AND RESBIEN_RESGUID = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, codigoBien);
            ps.setInt(2, idResguardo);
            ps.executeUpdate();
            exito = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return exito;
    }


    public List<Bien> obtenerBienesDeResguardo(int idResguardo) {
        List<Bien> bienes = new ArrayList<>();
        String query = "SELECT b.BIEN_CODIGO, b.DESCRIPCION, b.MARCA, b.MODELO, b.SERIE, b.ESTADO " +
                "FROM BIEN b " +
                "JOIN RESGUARDO_BIEN rb ON b.BIEN_CODIGO = rb.RESBIEN_CODBIEN " +
                "WHERE rb.RESBIEN_RESGUID = ? AND b.ESTADO = 1";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idResguardo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bien bien = new Bien();
                    bien.setBien_codigo(rs.getString("BIEN_CODIGO"));
                    bien.setDescripcion(rs.getString("DESCRIPCION"));
                    bien.setMarca(rs.getString("MARCA"));
                    bien.setModelo(rs.getString("MODELO"));
                    bien.setSerie(rs.getString("SERIE"));
                    bien.setEstado(rs.getInt("ESTADO"));
                    bienes.add(bien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bienes;
    }


    public Resguardo obtenerDatosResguardo(int idResguardo) {
        Resguardo resguardo = null;
        String query = "SELECT r.ID_RESGUARDO, r.FECHA, r.ESTADO, " +
                "e.RFC, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, " +
                "esp.ID_ESPACIO, esp.NOMBRE AS nombre_espacio, esp.ESTADO AS estado_espacio, " +
                "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio " +
                "FROM RESGUARDO r " +
                "JOIN EMPLEADO e ON r.RFC_EMPLEADO = e.RFC " +
                "JOIN ESPACIO esp ON r.ESPACIO_ID = esp.ID_ESPACIO " +
                "JOIN EDIFICIO ed ON esp.ID_EDIFICIO = ed.ID_EDIFICIO " +
                "WHERE r.ID_RESGUARDO = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idResguardo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resguardo = new Resguardo();
                    resguardo.setId(rs.getInt("ID_RESGUARDO"));
                    resguardo.setFecha(rs.getDate("FECHA").toLocalDate());
                    resguardo.setEstado(rs.getInt("ESTADO"));

                    Empleado empleado = new Empleado();
                    empleado.setRfc(rs.getString("RFC"));
                    empleado.setNombre(rs.getString("NOMBRE"));
                    empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                    empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                    resguardo.setEmpleado(empleado);

                    Edificio edificio = new Edificio();
                    edificio.setId(rs.getInt("ID_EDIFICIO"));
                    edificio.setNombre(rs.getString("nombre_edificio"));
                    edificio.setEstado(rs.getInt("estado_edificio"));

                    Espacio espacio = new Espacio();
                    espacio.setId(rs.getInt("ID_ESPACIO"));
                    espacio.setNombre(rs.getString("nombre_espacio"));
                    espacio.setEstado(rs.getInt("estado_espacio"));
                    espacio.setEdificio(edificio);

                    resguardo.setEspacio(espacio);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resguardo;
    }


    public static List<Bien> obtenerBienesPorResguardo(int idResguardo) {
        List<Bien> lista = new ArrayList<>();
        String query = "SELECT b.BIEN_CODIGO, b.DESCRIPCION, b.MARCA, b.MODELO, b.SERIE " +
                "FROM RESGUARDO_BIEN rb " +
                "JOIN BIEN b ON rb.RESBIEN_CODBIEN = b.BIEN_CODIGO " +
                "WHERE rb.RESBIEN_RESGUID = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idResguardo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Bien bien = new Bien();
                    bien.setBien_codigo(rs.getString("BIEN_CODIGO"));
                    bien.setDescripcion(rs.getString("DESCRIPCION"));
                    bien.setMarca(rs.getString("MARCA"));
                    bien.setModelo(rs.getString("MODELO"));
                    bien.setSerie(rs.getString("SERIE"));
                    lista.add(bien);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}