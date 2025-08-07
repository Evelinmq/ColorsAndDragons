package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.*;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResguardoDao {

    public static int insertarResguardo(Resguardo resguardo) {
        int idGenerado = -1;
        // Utiliza un PreparedStatement y solicita las claves generadas
        String sql = "INSERT INTO RESGUARDO (FECHA, RFC_EMPLEADO, ESPACIO_ID, ESTADO) VALUES (?, ?, ?, ?)";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{"ID_RESGUARDO"})) {

            ps.setDate(1, Date.valueOf(resguardo.getFecha()));
            ps.setString(2, resguardo.getEmpleado().getRfc());
            ps.setInt(3, resguardo.getEspacio().getId());
            ps.setInt(4, resguardo.getEstado());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idGenerado;
    }

    public boolean updateResguardo(int idResguardo, Date fecha, String rfcEmpleado, int idEspacio) {
        String query = "UPDATE RESGUARDO SET FECHA = ?, RFC_EMPLEADO = ?, ESPACIO_ID = ? WHERE ID_RESGUARDO = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setDate(1, fecha);
            ps.setString(2, rfcEmpleado);
            ps.setInt(3, idEspacio);
            ps.setInt(4, idResguardo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteResguardo(int id) {
        String query = "UPDATE RESGUARDO SET ESTADO = 0 WHERE ID_RESGUARDO = ?";
        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Resguardo> readResguardoPorEstado(int estado) {
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

    public List<Resguardo> readTodosResguardos() {
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
                "ORDER BY r.ID_RESGUARDO ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Resguardo> readResguardo() {
        return readResguardoPorEstado(1);
    }

    public boolean restaurarResguardo(int id) {
        String query = "UPDATE RESGUARDO SET ESTADO = 1 WHERE ID_RESGUARDO = ?";
        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Resguardo> readResguardoEspecifico(String texto) {
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
                "WHERE LOWER(es.NOMBRE) LIKE ? " +
                "   OR LOWER(ed.NOMBRE) LIKE ? " +
                "   OR LOWER(em.NOMBRE) LIKE ? " +
                "   OR LOWER(em.APELLIDO_PATERNO) LIKE ? " +
                "   OR LOWER(em.APELLIDO_MATERNO) LIKE ? " +
                "ORDER BY r.ID_RESGUARDO ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            String search = "%" + texto.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, search);
            }
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


    public static Resguardo obtenerPorId(int idResguardo) {
        Resguardo resguardo = null;
        String query = "SELECT r.ID_RESGUARDO, r.FECHA, r.RFC_EMPLEADO, r.ESPACIO_ID, " +
                "e.NOMBRE AS nombre_espacio, e.ESTADO AS estado_espacio, " +
                "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio, " +
                "em.NOMBRE AS nombre_empleado, em.APELLIDO_PATERNO, em.APELLIDO_MATERNO " +
                "FROM RESGUARDO r " +
                "JOIN EMPLEADO em ON r.RFC_EMPLEADO = em.RFC " +
                "JOIN ESPACIO e ON r.ESPACIO_ID = e.ID_ESPACIO " +
                "JOIN EDIFICIO ed ON e.ID_EDIFICIO = ed.ID_EDIFICIO " +
                "WHERE r.ID_RESGUARDO = ?";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idResguardo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    resguardo = new Resguardo();
                    resguardo.setId(rs.getInt("ID_RESGUARDO"));
                    resguardo.setFecha(rs.getDate("FECHA").toLocalDate());

                    Empleado empleado = new Empleado();
                    empleado.setRfc(rs.getString("RFC_EMPLEADO"));
                    empleado.setNombre(rs.getString("nombre_empleado"));
                    empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                    empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                    resguardo.setEmpleado(empleado);

                    Edificio edificio = new Edificio();
                    edificio.setId(rs.getInt("ID_EDIFICIO"));
                    edificio.setNombre(rs.getString("nombre_edificio"));
                    edificio.setEstado(rs.getInt("estado_edificio"));

                    Espacio espacio = new Espacio();
                    espacio.setId(rs.getInt("ESPACIO_ID"));
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
}