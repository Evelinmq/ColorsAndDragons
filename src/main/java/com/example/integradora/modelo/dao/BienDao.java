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

        String query = "INSERT INTO bien( bien_codigo, descripcion, marca, modelo, Serie, estado) VALUES (?,?,?,?,?,?)";

        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, b.getBien_codigo());
            ps.setString(2, b.getDescripcion());
            ps.setString(3, b.getMarca());
            ps.setString(4, b.getModelo());
            ps.setString(5, b.getSerie());
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

        String query = "SELECT * FROM BIEN ORDER BY bien_codigo ASC";
        List<Bien> bienes = new ArrayList<Bien>();

        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Bien b = new Bien();
                b.setBien_codigo(rs.getString("bien_codigo"));
                b.setDescripcion(rs.getString("Descripcion"));
                b.setMarca(rs.getString("Marca"));
                b.setModelo(rs.getString("Modelo"));
                b.setSerie(rs.getString("Serie"));
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

    public boolean updateBien(String codigoViejo, Bien b) {
        try {

            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE  bien set  bien_Codigo=?, Descripcion=?, Marca=?, Modelo=?, Serie=?, Estado=? WHERE bien_codigo=?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, b.getBien_codigo());
            ps.setString(2, b.getDescripcion());
            ps.setString(3, b.getMarca());
            ps.setString(4, b.getModelo());
            ps.setString(5, b.getSerie());
            ps.setInt(6, b.getEstado());
            ps.setString(7, codigoViejo);
            if (ps.executeUpdate() > 0) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }
    return false;
    }

    public static boolean deleteBien(String Bien_codigo) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE bien SET estado = 0 WHERE bien_codigo = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, Bien_codigo);
            if(ps.executeUpdate()>0){
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public static boolean regresoBien(String Bien_codigo) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE bien SET estado = 1 WHERE bien_codigo = ? AND estado = 0";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, Bien_codigo);
            if(ps.executeUpdate()>0){
                return true;
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public List<Bien> readBienEspecifico(String texto) {
        List<Bien> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM bien WHERE BIEN_CODIGO LIKE ? ORDER BY BIEN_CODIGO ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,"%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Bien b = new Bien();
                b.setBien_codigo(rs.getString("bien_codigo"));
                b.setDescripcion(rs.getString("descripcion"));
                b.setMarca(rs.getString("marca"));
                b.setModelo(rs.getString("modelo"));
                b.setSerie(rs.getString("serie"));
                b.setEstado(rs.getInt("estado"));
                lista.add(b);
            }
            rs.close();
            conn.close();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return lista;

    }

}
