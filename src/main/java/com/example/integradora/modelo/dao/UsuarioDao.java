package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Usuario;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public boolean createUsuario(Usuario u) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO usuario(correo, contrasena, rfc_empleado, id_unidad, id_puesto, estado) VALUES(?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRfcEmpleado());
            ps.setInt(4, u.getIdUnidad());
            ps.setInt(5, u.getIdPuesto());
            ps.setInt(6, u.getEstado());
            if (ps.executeUpdate() > 0) {
                System.out.println("Usuario creado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUsuario(String correoViejo, Usuario u) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE usuario SET contrasena = ?, rfc_empleado = ?, id_unidad = ?, id_puesto = ?, estado = ? WHERE correo = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getContrasena());
            ps.setString(2, u.getRfcEmpleado());
            ps.setInt(3, u.getIdUnidad());
            ps.setInt(4, u.getIdPuesto());
            ps.setInt(5, u.getEstado());
            ps.setString(6, correoViejo);
            if (ps.executeUpdate() > 0) {
                System.out.println("Usuario actualizado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUsuario(String correo) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "DELETE FROM usuario WHERE correo = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, correo);
            if (ps.executeUpdate() > 0) {
                System.out.println("Usuario eliminado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Usuario> readUsuario() {
        List<Usuario> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM usuario ORDER BY correo ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRfcEmpleado(rs.getString("rfc_empleado"));
                u.setIdUnidad(rs.getInt("id_unidad"));
                u.setIdPuesto(rs.getInt("id_puesto"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

}
