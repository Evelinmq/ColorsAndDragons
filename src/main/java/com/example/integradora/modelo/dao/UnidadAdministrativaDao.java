package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UnidadAdministrativaDao {
    public boolean createUnidad(UnidadAdministrativa u){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO UNIDAD_ADMINISTRATIVA(nombre, estado) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getEstado());
            if (ps.executeUpdate() > 0){
                System.out.println("Unidad Administrativa creada");
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUnidad(int idUnidadViejo, UnidadAdministrativa u){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE UNIDAD_ADMINISTRATIVA SET nombre = ?, estado = ? where id_unidad = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, u.getNombre());
            ps.setInt(2, u.getEstado());
            ps.setInt(3, idUnidadViejo);
            if (ps.executeUpdate() > 0){
                System.out.println("Unidad Administrativa actualizada");
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteUnidad(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE UNIDAD_ADMINISTRATIVA SET estado=0 WHERE id_UNIDAD=?"; //no olvidar el where
            PreparedStatement ps = conn.prepareStatement(query);    //delete logico
            ps.setInt(1,id);
            if(ps.executeUpdate()>0){
                System.out.println("Unidad Administrativa eliminada");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UnidadAdministrativa> readUnidad(){
        List<UnidadAdministrativa> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM UNIDAD_ADMINISTRATIVA ORDER BY id_UNIDAD ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("id_UNIDAD"));
                u.setNombre(rs.getString("nombre"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    public List<UnidadAdministrativa> readUnidadEspecifico(String texto) {

        List<UnidadAdministrativa> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM UNIDAD_ADMINISTRATIVA WHERE id_UNIDAD LIKE ? ORDER BY ID_UNIDAD ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("id_UNIDAD"));
                u.setNombre(rs.getString("nombre"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;

    }

    public static List<UnidadAdministrativa> readTodosUnidades() {
        List<UnidadAdministrativa> unidades = new ArrayList<>();

        String query = "SELECT ID_UNIDAD, NOMBRE, ESTADO FROM UNIDAD_ADMINISTRATIVA ORDER BY ID_UNIDAD ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement(); // Usamos Statement porque NO hay parámetros
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("ID_UNIDAD"));
                u.setNombre(rs.getString("nombre"));
                u.setEstado(rs.getInt("estado"));
                unidades.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer todas las unidades de la base de datos:");
            e.printStackTrace();
        }
        return unidades;
    }

    public static boolean regresoUnidad(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE UNIDAD_ADMINISTRATIVA SET estado=1 WHERE id_UNIDAD=? AND estado = 0"; //no olvidar el where
            PreparedStatement ps = conn.prepareStatement(query);    //delete logico
            ps.setInt(1,id);
            if(ps.executeUpdate()>0){
                System.out.println("Unidad Administrativa recuperada");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<UnidadAdministrativa> readUnidadPorEstado(int estado) {
        List<UnidadAdministrativa> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM UNIDAD_ADMINISTRATIVA WHERE estado = ? ORDER BY id_UNIDAD ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("id_UNIDAD"));
                u.setNombre(rs.getString("nombre"));
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

    public static List<UnidadAdministrativa> readUnidadesActivas() {
        List<UnidadAdministrativa> unidades = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT ID_UNIDAD, NOMBRE, ESTADO FROM UNIDAD_ADMINISTRATIVA WHERE ESTADO = 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                UnidadAdministrativa unidad = new UnidadAdministrativa();
                unidad.setId(rs.getInt("ID_UNIDAD"));
                unidad.setNombre(rs.getString("NOMBRE"));
                unidad.setEstado(rs.getInt("ESTADO"));
                unidades.add(unidad);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return unidades;
    }

}
