package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDao {

    public boolean createEmpleado(Empleado e) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO empleado (rfc, nombre, apellido_paterno, apellido_materno, id_puesto, id_unidad, estado) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, e.getRfc());
            ps.setString(2, e.getNombre());
            ps.setString(3, e.getApellidoPaterno());
            ps.setString(4, e.getApellidoMaterno());
            ps.setInt(5, e.getIdPuesto());
            ps.setInt(6, e.getIdUnidadAdministrativa());
            ps.setInt(7, e.getEstado());
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateEmpleado(String rfcViejo, Empleado e) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE empleado SET nombre = ?, apellido_paterno = ?, apellido_materno = ?, " +
                    "id_puesto = ?, id_unidad= ?, estado = ? WHERE rfc = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getApellidoPaterno());
            ps.setString(3, e.getApellidoMaterno());
            ps.setInt(4, e.getIdPuesto());
            ps.setInt(5, e.getIdUnidadAdministrativa());
            ps.setInt(6, e.getEstado());
            ps.setString(7, rfcViejo);
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public static boolean deleteEmpleado(String rfc) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE empleado SET estado = 0 WHERE rfc = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,rfc);
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public static List<Empleado> readEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.RFC, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.ESTADO, " +
                    "p.ID_PUESTO, p.NOMBRE AS nombre_puesto, p.ESTADO AS estado_puesto, " +
                    "u.ID_UNIDAD, u.NOMBRE AS nombre_unidad, u.ESTADO AS estado_unidad " +
                    "FROM EMPLEADO e " +
                    "LEFT JOIN PUESTO p ON e.ID_PUESTO = p.ID_PUESTO " +
                    "LEFT JOIN UNIDAD_ADMINISTRATIVA u ON e.ID_UNIDAD = u.ID_UNIDAD " +
                    "ORDER BY e.RFC ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Crear objeto PUESTO
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Crear objeto UNIDAD
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Crear objeto EMPLEADO
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("NOMBRE"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("ESTADO"));
                empleado.setPuesto(puesto);
                empleado.setUnidadAdministrativa(unidad);

                lista.add(empleado);
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Empleado> readEmpleadoPorEstado(int estado) {
        List<Empleado> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.RFC, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.ESTADO, " +
                    "p.ID_PUESTO, p.NOMBRE AS nombre_puesto, p.ESTADO AS estado_puesto, " +
                    "u.ID_UNIDAD, u.NOMBRE AS nombre_unidad, u.ESTADO AS estado_unidad " +
                    "FROM EMPLEADO e " +
                    "LEFT JOIN PUESTO p ON e.ID_PUESTO = p.ID_PUESTO " +
                    "LEFT JOIN UNIDAD_ADMINISTRATIVA u ON e.ID_UNIDAD = u.ID_UNIDAD " +
                    "WHERE e.ESTADO = ? " +
                    "ORDER BY e.RFC ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Crear objeto PUESTO
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Crear objeto UNIDAD ADMINISTRATIVA
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Crear objeto EMPLEADO
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("NOMBRE"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("ESTADO"));
                empleado.setPuesto(puesto);
                empleado.setUnidadAdministrativa(unidad);

                lista.add(empleado);
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Empleado> readEmpleado() {
        return readEmpleadoPorEstado(1);
    }

    public static boolean regresoEmpleado(String Rfc) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE EMPLEADO SET estado=1 WHERE Rfc=? AND estado = 0";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, Rfc);
            if (ps.executeUpdate() > 0) {
                System.out.println("Empleado recuperado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<Empleado> readTodosEmpleados() {
        List<Empleado> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.RFC, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.ESTADO, " +
                    "p.ID_PUESTO, p.NOMBRE AS nombre_puesto, p.ESTADO AS estado_puesto, " +
                    "u.ID_UNIDAD, u.NOMBRE AS nombre_unidad, u.ESTADO AS estado_unidad " +
                    "FROM EMPLEADO e " +
                    "LEFT JOIN PUESTO p ON e.ID_PUESTO = p.ID_PUESTO " +
                    "LEFT JOIN UNIDAD_ADMINISTRATIVA u ON e.ID_UNIDAD = u.ID_UNIDAD " +
                    "ORDER BY e.RFC ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Crear objeto PUESTO
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Crear objeto UNIDAD ADMINISTRATIVA
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Crear objeto EMPLEADO
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("NOMBRE"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("ESTADO"));
                empleado.setPuesto(puesto);
                empleado.setUnidadAdministrativa(unidad);

                lista.add(empleado);
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Empleado> readEmpleadoEspecifico(String texto) {
        List<Empleado> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.RFC, e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO, e.ESTADO, " +
                    "p.ID_PUESTO, p.NOMBRE AS nombre_puesto, p.ESTADO AS estado_puesto, " +
                    "u.ID_UNIDAD, u.NOMBRE AS nombre_unidad, u.ESTADO AS estado_unidad " +
                    "FROM EMPLEADO e " +
                    "LEFT JOIN PUESTO p ON e.ID_PUESTO = p.ID_PUESTO " +
                    "LEFT JOIN UNIDAD_ADMINISTRATIVA u ON e.ID_UNIDAD = u.ID_UNIDAD " +
                    "WHERE LOWER(e.NOMBRE) LIKE ? OR LOWER(e.APELLIDO_PATERNO) LIKE ? " +
                    "OR LOWER(e.APELLIDO_MATERNO) LIKE ? OR LOWER(p.NOMBRE) LIKE ? " +
                    "OR LOWER(u.NOMBRE) LIKE ? " +
                    "ORDER BY e.RFC ASC";

            PreparedStatement ps = conn.prepareStatement(query);
            String search = "%" + texto.toLowerCase() + "%";
            for (int i = 1; i <= 5; i++) {
                ps.setString(i, search);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Crear objeto PUESTO
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("nombre_puesto"));
                puesto.setEstado(rs.getInt("estado_puesto"));

                // Crear objeto UNIDAD ADMINISTRATIVA
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("nombre_unidad"));
                unidad.setEstado(rs.getInt("estado_unidad"));

                // Crear objeto EMPLEADO
                Empleado empleado = new Empleado();
                empleado.setRfc(rs.getString("RFC"));
                empleado.setNombre(rs.getString("NOMBRE"));
                empleado.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
                empleado.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));
                empleado.setEstado(rs.getInt("ESTADO"));
                empleado.setPuesto(puesto);
                empleado.setUnidadAdministrativa(unidad);

                lista.add(empleado);
            }

            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }



}