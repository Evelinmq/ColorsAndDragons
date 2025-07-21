package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            String query = "UPDATE PUESTO SET id_puesto = ?, nombre = ?, estado = ? where id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, p.getId());
            ps.setString(2, p.getNombre());
            ps.setInt(3, p.getEstado());
            ps.setInt(4, idPuestoViejo);
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

    public boolean deletePuesto(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE PUESTO SET status=0 WHERE id_puesto=?"; //no olvidar el where
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
            String query = "SELECT * FROM PUESTO WHERE id LIKE ? OR " +
                    "nombre LIKE ? OR " +
                    "estado LIKE ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
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

    public boolean recoverPuesto(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE PUESTO SET status=1 WHERE id=?"; //no olvidar el where
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

}
