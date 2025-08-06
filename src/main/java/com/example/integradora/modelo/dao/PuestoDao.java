package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PuestoDao {

    public boolean createPuesto(Puesto p){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO PUESTO(nombre, estado) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getEstado());
            //ps.setInt(2, p.getEstado());
            if (ps.executeUpdate() > 0){
                System.out.println("Puesto creado");
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePuesto(int idPuestoViejo, Puesto p){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE PUESTO SET nombre = ?, estado = ? where id_puesto = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getEstado());
            ps.setInt(3, idPuestoViejo);
            if (ps.executeUpdate() > 0){
                System.out.println("Puesto actualizado");
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deletePuesto(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE PUESTO SET estado=0 WHERE id_puesto=?"; //no olvidar el where
            PreparedStatement ps = conn.prepareStatement(query);    //delete logico
            ps.setInt(1,id);
            if(ps.executeUpdate()>0){
                System.out.println("Puesto eliminado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Puesto> readPuesto(){
        List<Puesto> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM PUESTO ORDER BY id_puesto ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Puesto p = new Puesto();
                p.setId(rs.getInt("id_puesto"));
                p.setNombre(rs.getString("nombre"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    public List<Puesto> readPuestoEspecifico(String texto) {

        List<Puesto> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM PUESTO WHERE id_puesto LIKE ? ORDER BY ID_PUESTO ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%" + texto + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Puesto p = new Puesto();
                p.setId(rs.getInt("id_puesto"));
                p.setNombre(rs.getString("nombre"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;

    }

    public static List<Puesto> readTodosPuestos() {
        List<Puesto> puestos = new ArrayList<>();

        String query = "SELECT ID_PUESTO, NOMBRE, ESTADO FROM PUESTO ORDER BY ID_PUESTO ASC";

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             Statement stmt = conn.createStatement(); // Usamos Statement porque NO hay parámetros
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Puesto p = new Puesto();
                p.setId(rs.getInt("ID_PUESTO"));
                p.setNombre(rs.getString("nombre"));
                p.setEstado(rs.getInt("estado"));
                puestos.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al leer todos los puestos de la base de datos:");
            e.printStackTrace();
        }
        return puestos;
    }

    public static boolean regresoPuesto(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE PUESTO SET estado=1 WHERE id_puesto=? AND estado = 0"; //no olvidar el where
            PreparedStatement ps = conn.prepareStatement(query);    //delete logico
            ps.setInt(1,id);
            if(ps.executeUpdate()>0){
                System.out.println("Puesto recuperado");
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Puesto> readPuestoPorEstado(int estado) {
        List<Puesto> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM PUESTO WHERE estado = ? ORDER BY id_puesto ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Puesto p = new Puesto();
                p.setId(rs.getInt("id_puesto"));
                p.setNombre(rs.getString("nombre"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public static List<Puesto> readPuestosActivos() {
        List<Puesto> puestos = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT ID_PUESTO, NOMBRE, ESTADO FROM PUESTO WHERE ESTADO = 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Puesto puesto = new Puesto();
                puesto.setId(rs.getInt("ID_PUESTO"));
                puesto.setNombre(rs.getString("NOMBRE"));
                puesto.setEstado(rs.getInt("ESTADO"));
                puestos.add(puesto);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return puestos;
    }

}
