package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UnidadAdministrativaDao {

    public boolean createUnidadAdministrativa(UnidadAdministrativa ua) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO Unidad_Administrativa(nombre, estado) values(?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, ua.getNombre());
            ps.setInt(2, ua.getEstado());
            if(ps.executeUpdate() > 0){
                conn.close(); // <---
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUnidadAdministrativa(int idViejito, UnidadAdministrativa ua) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE Unidad_Administrativa SET nombre = ?, estado = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, ua.getNombre());
            ps.setInt(2, ua.getEstado());
            ps.setInt(3, idViejito);
            if(ps.executeUpdate() > 0){
                conn.close();
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteUnidadAdministrativa(int id) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "DELETE FROM Unidad_Administrativa WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() > 0){
                conn.close(); // <---
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<UnidadAdministrativa> readUnidadAdministrativa() {
        List<UnidadAdministrativa> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT *FROM Unidad_Administrativa ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
            conn.close();
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<UnidadAdministrativa> readUnidadAdministrativaEspecifico(String texto) {

        List<UnidadAdministrativa> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM Unidad_Adminsitrativa WHERE nombre LIKE ? OR estado LIKE ? OR " +
                 "estado LIKE ? OR +" +
                 "id LIKE ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                UnidadAdministrativa u = new UnidadAdministrativa();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }


}
