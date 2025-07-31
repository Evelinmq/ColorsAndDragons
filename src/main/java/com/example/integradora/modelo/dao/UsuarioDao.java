package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Usuario;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDao {

    public boolean createUsuario(Usuario u) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO usuario(correo, contrasenia, rol, rfc_empleado, estado) VALUES(?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getCorreo());
            ps.setString(2, u.getContrasena());
            ps.setString(3, u.getRol());
            ps.setString(4, u.getRfcEmpleado());
            ps.setInt(5, u.getEstado());
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
            String query = "UPDATE usuario SET contrasenia = ?,rol=?, rfc_empleado = ?, estado = ? WHERE correo = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getContrasena());
            ps.setString(2, u.getRol());
            ps.setString(3, u.getRfcEmpleado());
            ps.setInt(4, u.getEstado());
            ps.setString(5, correoViejo);
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
                u.setContrasena(rs.getString("contrasenia"));
                u.setRfcEmpleado(rs.getString("rfc_empleado"));
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

    public List<Usuario> readUsuarioEspecifico(String texto) {
        List<Usuario> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM USUARIO WHERE correo LIKE ? ORDER BY correo ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
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

    public static List<Usuario> readTodosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        String query = "SELECT CORREO, CONTRASENIA, ESTADO FROM USUARIO ORDER BY ID_USUARIO ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasenia"));
                u.setEstado(rs.getInt("estado"));
                usuarios.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer todos los usuarios de la base de datos:");
            e.printStackTrace();
        }
        return usuarios;
    }

    public static boolean regresoUsuario(String correo) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE USUARIO SET estado=1 WHERE correo=? AND estado = 0";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, correo);
            if (ps.executeUpdate() > 0) {
                System.out.println("Usuario recuperado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Usuario> readUsuarioPorEstado(int estado) {
        List<Usuario> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM USUARIO WHERE estado = ? ORDER BY correo ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario u = new Usuario();
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
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
