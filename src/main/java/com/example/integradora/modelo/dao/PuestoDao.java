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

    public boolean createPuesto(Puesto p) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "Insert into Tabla_Puesto(nombre, estado) values (?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getEstado());
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        return false;
    }


    public boolean updatePuesto(int idViejito, Puesto p) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE Tabla_Puesto SET nombre = ?, estado = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, p.getNombre());
            ps.setInt(2, p.getEstado());
            ps.setInt(3, idViejito);
            if(ps.executeUpdate() > 0) {
                conn.close(); // <---
                return true;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePuesto(int id) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "DELETE FROM Tabla_Puesto WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return false;
    }


    public List<Puesto> readPuestos() {
        List<Puesto> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM Tabla_Puesto ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Puesto p = new Puesto();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setEstado(rs.getInt("estado"));
                lista.add(p);
            }
            conn.close();
            rs.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }






    public List<Puesto> readPuestoEspecifico(String texto){
        List<Puesto> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM Tabla_Puesto WHERE puesto LIKE ? OR "+
                   "estado LIKE ? OR +" +
                    "id LIKE ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Puesto p = new Puesto();
                p.setId(rs.getInt("id"));
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

}
