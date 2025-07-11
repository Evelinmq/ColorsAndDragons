package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EdificioDao {
    public boolean createEdificio(Edificio m){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "Insert into EDIFICIO(id_edificio, nombre, estado) values(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getEstado());
            if (ps.executeUpdate() > 0){
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEdificio(int idEdificioViejo, Edificio m){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE EDIFICIO SET id_edificio = ?, nombre = ?, estado = ? where id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getEstado());
            ps.setInt(4, idEdificioViejo);
            if (ps.executeUpdate() > 0){
                conn.close();
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteEdificio(int id){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "DELETE FROM EDIFICIO WHERE id_edificio = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0){
                conn.close(); // <---
                return true;
            }
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Edificio> readEdificio(){
        List<Edificio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Edificio m = new Edificio();
                m.setId(rs.getInt("id"));
                m.setNombre(rs.getString("nombre"));
                m.setEstado(rs.getInt("estado"));
                lista.add(m);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }

    public List<Edificio> readEdificioEspecifico(String texto) {

        List<Edificio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO WHERE id LIKE ? OR " +
                    "nombre LIKE ? OR " +
                    "estado LIKE ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Edificio m = new Edificio();
                m.setId(rs.getInt("id"));
                m.setNombre(rs.getString("nombre"));
                m.setEstado(rs.getInt("estado"));
                lista.add(m);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;

    }
}
