package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EspacioDao {
    public boolean createEspacio(Espacio m){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "Insert into EDIFICIO(ID_ESPACIO, NOMBRE, EDIFICIO_ID_EDIFICIO, ESTADO) values(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getIdEdificio());
            ps.setInt(4, m.getEstado());
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

    public boolean updateEspacio(int idEspacioViejo, Espacio m){
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE EDIFICIO SET ID_ESPACIO = ?, NOMBRE = ?, EDIFICIO_ID_EDIFICIO = ?, ESTADO = ? where ID_ESPACIO = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getIdEdificio());
            ps.setInt(4, m.getEstado());
            ps.setInt(5, idEspacioViejo);
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

    public boolean deleteEspacio(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET status=0 WHERE id=?"; //no olvidar el where
            PreparedStatement ps = conn.prepareStatement(query);    //delete logico
            ps.setInt(1,id);
            if(ps.executeUpdate()>0){
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Espacio> readEspacio(){
        List<Espacio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM ESPACIO ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Espacio m = new Espacio();
                m.setId(rs.getInt("id"));
                m.setNombre(rs.getString("nombre"));
                m.setId(rs.getInt("idEspacio"));
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

    public List<Espacio> readEspacioEspecifico(String texto) {

        List<Espacio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM ESPACIO WHERE ID_ESPACIO LIKE ? OR " +
                    "NOMBRE LIKE ? OR " +
                    "EDIFICIO_ID_EDIFICIO LIKE ? OR " +
                    "ESTADO LIKE ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
            ps.setString(4, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Espacio m = new Espacio();
                m.setId(rs.getInt("id"));
                m.setNombre(rs.getString("nombre"));
                m.setId(rs.getInt("idEspacio"));
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
