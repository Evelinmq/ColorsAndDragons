package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.*;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResguardoDao {

    public boolean createResguardo(Resguardo r) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO RESGUARDO(FECHA, ESTADO) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, r.getId());
            ps.setInt(2, r.getEstado());

            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean updateResguardo(int idResguardoViejo, Espacio m, Empleado e) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET NOMBRE = ?, ESTADO = ?, ID_EDIFICIO = ? WHERE ID_ESPACIO = ?";
            String queryDos = "UPDATE EMPLEADO SET NOMBRE = ?, APELLIDO_PATERNO = ?, APELLIDO_MATERNO = ?, ID_PUESTO = ?, ID_UNIDAD = ?, ESTADO = 1  WHERE RFC = ? ";
            PreparedStatement ps = conn.prepareStatement(query);
            PreparedStatement psdos = conn.prepareStatement(queryDos);
            ps.setString(1, m.getNombre());
            ps.setInt(2, m.getEstado());
            ps.setInt(3, m.getEdificio().getId());
            ps.setInt(4, idResguardoViejo);
            psdos.setString(1, e.getNombre());
            psdos.setString(2, e.getApellidoPaterno());
            psdos.setString(3, e.getApellidoMaterno());
            psdos.setInt(4, e.getIdPuesto());
            psdos.setInt(5, e.getIdUnidadAdministrativa());
            psdos.setInt(6, e.getEstado());

            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean deleteResguardo(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE RESGUARDO SET ESTADO = 0 WHERE ID_RESGUARDO = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Resguardo> readResguardoPorEstado(int estado) {
        List<Resguardo> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();

            String query = "SELECT " +
                    "r.ID_RESGUARDO, r.FECHA, r.ESTADO AS estado_resguardo, " +

                    "es.ID_ESPACIO, es.NOMBRE AS nombre_espacio, es.ESTADO AS estado_espacio, " +
                    "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio, " +

                    "em.RFC, em.NOMBRE AS nombre_empleado, em.APELLIDO_PATERNO, em.APELLIDO_MATERNO, em.ESTADO AS estado_empleado, " +
                    "pu.ID_PUESTO, pu.NOMBRE AS nombre_puesto, pu.ESTADO AS estado_puesto, " +
                    "ua.ID_UNIDAD, ua.NOMBRE AS nombre_unidad, ua.ESTADO AS estado_unidad " +

                    "FROM RESGUARDO r " +
                    "JOIN ESPACIO es ON r.ID_ESPACIO = es.ID_ESPACIO " +
                    "JOIN EDIFICIO ed ON es.ID_EDIFICIO = ed.ID_EDIFICIO " +
                    "JOIN EMPLEADO em ON r.ID_EMPLEADO = em.RFC " +
                    "JOIN PUESTO pu ON em.ID_PUESTO = pu.ID_PUESTO " +
                    "JOIN UNIDAD_ADMINISTRATIVA ua ON em.ID_UNIDAD = ua.ID_UNIDAD " +
                    "WHERE r.ESTADO = ? " +
                    "ORDER BY r.ID_RESGUARDO ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Crear edificio
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                // Crear espacio
                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("nombre_espacio"));
                espacio.setEstado(rs.getInt("estado_espacio"));
                espacio.setEdificio(edificio);

                // Crear puesto
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Crear unidad
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Crear empleado
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("nombre_empleado"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("estado_empleado"));
                empleado.setPuesto(puesto);
                empleado.setUnidad(unidad);

                // Crear resguardo
                Resguardo resguardo = new Resguardo();
                resguardo.setId(rs.getInt("ID_RESGUARDO"));
                resguardo.setFecha(rs.getDate("FECHA").toLocalDate());
                resguardo.setEstado(rs.getInt("estado_resguardo"));
                resguardo.setEspacio(espacio);
                resguardo.setEmpleado(empleado);

                lista.add(resguardo);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Resguardo> readTodosResguardos() {
        List<Resguardo> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT " +
                    "r.ID_RESGUARDO, r.FECHA, r.ESTADO AS estado_resguardo, " +

                    "es.ID_ESPACIO, es.NOMBRE AS nombre_espacio, es.ESTADO AS estado_espacio, " +
                    "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio, " +

                    "em.RFC, em.NOMBRE AS nombre_empleado, em.APELLIDO_PATERNO, em.APELLIDO_MATERNO, em.ESTADO AS estado_empleado, " +
                    "pu.ID_PUESTO, pu.NOMBRE AS nombre_puesto, pu.ESTADO AS estado_puesto, " +
                    "ua.ID_UNIDAD, ua.NOMBRE AS nombre_unidad, ua.ESTADO AS estado_unidad " +

                    "FROM RESGUARDO r " +
                    "JOIN ESPACIO es ON r.ID_ESPACIO = es.ID_ESPACIO " +
                    "JOIN EDIFICIO ed ON es.ID_EDIFICIO = ed.ID_EDIFICIO " +
                    "JOIN EMPLEADO em ON r.ID_EMPLEADO = em.RFC " +
                    "JOIN PUESTO pu ON em.ID_PUESTO = pu.ID_PUESTO " +
                    "JOIN UNIDAD_ADMINISTRATIVA ua ON em.ID_UNIDAD = ua.ID_UNIDAD " +
                    "ORDER BY r.ID_RESGUARDO ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Edificio
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                // Espacio
                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("nombre_espacio"));
                espacio.setEstado(rs.getInt("estado_espacio"));
                espacio.setEdificio(edificio);

                // Puesto
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Unidad
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Empleado
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("nombre_empleado"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("estado_empleado"));
                empleado.setPuesto(puesto);
                empleado.setUnidad(unidad);

                // Resguardo
                Resguardo resguardo = new Resguardo();
                resguardo.setId(rs.getInt("ID_RESGUARDO"));
                resguardo.setFecha(rs.getDate("FECHA").toLocalDate());
                resguardo.setEstado(rs.getInt("estado_resguardo"));
                resguardo.setEspacio(espacio);
                resguardo.setEmpleado(empleado);

                lista.add(resguardo);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Resguardo> readResguardo() {
        return readResguardoPorEstado(1);
    }

    public boolean restaurarResguardo(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE RESGUARDO SET ESTADO = 1 WHERE ID_RESGUARDO = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Resguardo> readResguardoEspecifico(String texto) {
        List<Resguardo> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
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
                    "JOIN EMPLEADO em ON r.ID_EMPLEADO = em.RFC " +
                    "JOIN PUESTO pu ON em.ID_PUESTO = pu.ID_PUESTO " +
                    "JOIN UNIDAD_ADMINISTRATIVA ua ON em.ID_UNIDAD = ua.ID_UNIDAD " +
                    "WHERE LOWER(es.NOMBRE) LIKE ? " +
                    "   OR LOWER(ed.NOMBRE) LIKE ? " +
                    "   OR LOWER(em.NOMBRE) LIKE ? " +
                    "   OR LOWER(em.APELLIDO_PATERNO) LIKE ? " +
                    "   OR LOWER(em.APELLIDO_MATERNO) LIKE ? " +
                    "ORDER BY r.ID_RESGUARDO ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            String search = "%" + texto.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, search);
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Edificio
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                // Espacio
                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("nombre_espacio"));
                espacio.setEstado(rs.getInt("estado_espacio"));
                espacio.setEdificio(edificio);

                // Puesto
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Unidad
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Empleado
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("nombre_empleado"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("estado_empleado"));
                empleado.setPuesto(puesto);
                empleado.setUnidad(unidad);

                // Resguardo
                Resguardo resguardo = new Resguardo();
                resguardo.setId(rs.getInt("ID_RESGUARDO"));
                resguardo.setFecha(rs.getDate("FECHA").toLocalDate());
                resguardo.setEstado(rs.getInt("estado_resguardo"));
                resguardo.setEspacio(espacio);
                resguardo.setEmpleado(empleado);

                lista.add(resguardo);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    //OBTENER LA INFORMACIÓN BÁSICA DEL RESGUARDO: FECHA, EMPLEADO, ESPACIO
    public static Resguardo obtenerPorId(int idResguardo) {
        Resguardo resguardo = null;
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT r.ID_RESGUARDO, r.FECHA, r.EMPLEADO_RFC, r.ESPACIO_ID, " +
                    "e.NOMBRE AS nombre_espacio, e.ESTADO AS estado_espacio, " +
                    "ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio, " +
                    "em.NOMBRE AS nombre_empleado, em.APELLIDO_PATERNO, em.APELLIDO_MATERNO " +
                    "FROM RESGUARDO r " +
                    "JOIN EMPLEADO em ON r.EMPLEADO_RFC = em.RFC " +
                    "JOIN ESPACIO e ON r.ID_ESPACIO = e.ID_ESPACIO " +
                    "JOIN EDIFICIO ed ON e.ID_EDIFICIO = ed.ID_EDIFICIO " +
                    "WHERE r.ID_RESGUARDO = ?";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, idResguardo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                resguardo = new Resguardo();
                resguardo.setId(rs.getInt("ID_RESGUARDO"));
                resguardo.setFecha(rs.getDate("FECHA").toLocalDate());

                // Empleado
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("EMPLEADO_RFC"));
                empleado.setNombre(rs.getString("nombre_empleado"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                resguardo.setEmpleado(empleado);

                // Edificio
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                // Espacio
                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("nombre_espacio"));
                espacio.setEstado(rs.getInt("estado_espacio"));
                espacio.setEdificio(edificio);
                resguardo.setEspacio(espacio);
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resguardo;
    }

    public static int insertarResguardo(Resguardo resguardo) {
        int idGenerado = -1;
        try (Connection conn = OracleDatabaseConnectionManager.getConnection()) {
            String sql = "INSERT INTO RESGUARDO (FECHA, RFC, ESPACIO_ID, ESTADO) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, new String[] { "ID_RESGUARDO" });

            ps.setDate(1, Date.valueOf(resguardo.getFecha()));
            ps.setString(2, resguardo.getEmpleado().getRfc());
            ps.setInt(3, resguardo.getEspacio().getId());
            ps.setInt(4, resguardo.getEstado());

            int filas = ps.executeUpdate();

            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1); // ID_RESGUARDO generado
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idGenerado;
    }

    public int insertResguardo(Resguardo resguardo) {
        int idGenerado = -1;

        String query = "INSERT INTO RESGUARDO (FECHA, RFC_EMPLEADO, ESPACIO_ID, ESTADO) VALUES (?, ?, ?, ?)";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query, new String[] { "ID_RESGUARDO" })) {

            ps.setDate(1, Date.valueOf(resguardo.getFecha())); // java.sql.Date
            ps.setString(2, resguardo.getEmpleado().getRfc());
            ps.setInt(3, resguardo.getEspacio().getId());
            ps.setInt(4, resguardo.getEstado());

            int rowsInserted = ps.executeUpdate();

            if (rowsInserted > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    idGenerado = rs.getInt(1); // Obtener ID generado por la secuencia
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return idGenerado;
    }




}