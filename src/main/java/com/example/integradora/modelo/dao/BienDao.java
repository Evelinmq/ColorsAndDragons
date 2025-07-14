package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Bien;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BienDao {

    public boolean bienCreate(Bien b) {

        String query = "INSERT INTO bien(id, codigo, descripción, marca, modelo, noSerie, estado) VALUES (?,?,?,?,?,?,?)";

        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, b.getId());
            ps.setString(2, b.getCodigo());
            ps.setString(3, b.getDescripcion());
            ps.setString(4, b.getMarca());
            ps.setString(5, b.getModelo());
            ps.setString(6, b.getNoSerie());
            ps.setInt(7, b.getEstado());

            if (ps.executeUpdate() > 0) {
                System.out.println("Bien registrado");
                conn.close();
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<Bien>  readBien(){

        String query = "SELECT * FROM bien ORDER BY id ASC";
        List<Bien> bienes = new ArrayList<Bien>();

        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Bien b = new Bien();
                b.setId(rs.getInt("Id"));
                b.setCodigo(rs.getString("Codigo"));
                b.setDescripcion(rs.getString("Descripcion"));
                b.setMarca(rs.getString("Marca"));
                b.setModelo(rs.getString("Modelo"));
                b.setNoSerie(rs.getString("NoSerie"));
                b.setEstado(rs.getInt("Estado"));
                bienes.add (b);

            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return bienes;
    }

    public boolean updateBien(int idViejo, Bien b) {
        try {

            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE  bien set id=?, Codigo=?, Descripcion=?, Marca=?, Modelo=?, NoSerie=?, Estado=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, b.getId());
            ps.setString(2, b.getCodigo());
            ps.setString(3, b.getDescripcion());
            ps.setString(4, b.getMarca());
            ps.setString(5, b.getModelo());
            ps.setString(6, b.getNoSerie());
            ps.setInt(7, b.getEstado());
            ps.setInt(8, idViejo);
            if (ps.executeUpdate() > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
    return false;
    }

    public boolean deleteBien(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "DELETE bien SET status = 0 WHERE id=? ";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate()>0){
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

}
