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

        String query = "INSERT INTO bien( bien_codigo, descripción, marca, modelo, Serie, estado) VALUES (?,?,?,?,?,?)";

        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, b.getCodigo());
            ps.setString(2, b.getDescripcion());
            ps.setString(3, b.getMarca());
            ps.setString(4, b.getModelo());
            ps.setString(5, b.getNoSerie());
            ps.setInt(6, b.getEstado());

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

        String query = "SELECT * FROM bien ORDER BY bien_codigo ASC";
        List<Bien> bienes = new ArrayList<Bien>();

        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Bien b = new Bien();
                b.setCodigo(rs.getString("bien_codigo"));
                b.setDescripcion(rs.getString("Descripcion"));
                b.setMarca(rs.getString("Marca"));
                b.setModelo(rs.getString("Modelo"));
                b.setNoSerie(rs.getString("Serie"));
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
            String query = "UPDATE  bien set  bien_Codigo=?, Descripcion=?, Marca=?, Modelo=?, Serie=?, Estado=? WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, b.getCodigo());
            ps.setString(2, b.getDescripcion());
            ps.setString(3, b.getMarca());
            ps.setString(4, b.getModelo());
            ps.setString(5, b.getNoSerie());
            ps.setInt(6, b.getEstado());
            ps.setInt(7, idViejo);
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
            String query = "DELETE bien SET status = 0 WHERE bien_codigo = ? ";
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
