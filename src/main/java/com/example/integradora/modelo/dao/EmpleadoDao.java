package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmpleadoDao {

    public boolean createEmpleado(Empleado e) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO empleado (rfc, nombre, apellido_paterno, apellido_materno, id_puesto, id_unidad_administrativa, estado) " +
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
                    "id_puesto = ?, id_unidad_administrativa = ?, estado = ? WHERE rfc = ?";
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
            String query = "DELETE FROM empleado WHERE rfc = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, rfc);
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
            String query = "SELECT * FROM empleado ORDER BY rfc ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Empleado e = new Empleado();
                e.setRfc(rs.getString("rfc"));
                e.setNombre(rs.getString("nombre"));
                e.setApellidoPaterno(rs.getString("apellido_paterno"));
                e.setApellidoMaterno(rs.getString("apellido_materno"));
                e.setIdPuesto(rs.getInt("id_puesto"));
                e.setIdUnidadAdministrativa(rs.getInt("id_unidad_administrativa"));
                e.setEstado(rs.getInt("estado"));
                lista.add(e);
            }
            rs.close();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lista;
    }
}