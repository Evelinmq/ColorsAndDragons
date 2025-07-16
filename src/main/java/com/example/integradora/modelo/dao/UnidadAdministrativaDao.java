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
            String query = "INSERT INTO Unidad_Administrativa(nombre, estado) values(?,1)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, ua.getNombre());
            if(ps.executeUpdate() > 0){
                System.out.println("La Unidad Administrativa ha sido creada con exito");
                conn.close(); // <---
                return true;
            }
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUnidadAdministrativa(int idViejito, UnidadAdministrativa ua) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE Unidad_Administrativa SET nombre = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, ua.getNombre());
            ps.setInt(2, idViejito);
            if(ps.executeUpdate() >= 1){
                System.out.println("Se actualizó la Unidad Administrativa con exito");
                conn.close();
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //Delete LÓGICO
    public boolean deleteUnidadAdministrativa(int id) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE Unidad_Administrativa SET status = 0 WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() > 0){
                //conn.close(); // <---
                return true;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    //Recuperar Unidad Administrativa borrada
    public boolean recoverUnidadAdministrativa(int id) {
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE Unidad_Administrativa SET status = 1 WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() > 0){
                //conn.close(); // <---
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
                u.setNombre(rs.getString("nombre"));
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
                //u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                //u.setEstado(rs.getInt("estado"));
                lista.add(u);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;
    }


}
