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
            String query = "INSERT INTO EDIFICIO(nombre, estado) VALUES(?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, m.getNombre());
            ps.setInt(2, m.getEstado());
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
            String query = "UPDATE EDIFICIO SET nombre = ?, estado = ? WHERE id_edificio = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, m.getNombre());
            ps.setInt(2, m.getEstado());
            ps.setInt(3, idEdificioViejo);
            if (ps.executeUpdate() > 0){
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteEdificio(int id){
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE EDIFICIO SET estado = 0 WHERE id_edificio = ?"; // delete lógico
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if(ps.executeUpdate() > 0){
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<Edificio> readEdificioPorEstado(int estado) {
        List<Edificio> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO WHERE estado = ? ORDER BY id_edificio ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Edificio m = new Edificio();
                m.setId(rs.getInt("id_edificio"));
                m.setNombre(rs.getString("nombre"));
                m.setEstado(rs.getInt("estado"));
                lista.add(m);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Edificio> readTodosEdificios() {
        List<Edificio> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO ORDER BY id_edificio ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Edificio m = new Edificio();
                m.setId(rs.getInt("id_edificio"));
                m.setNombre(rs.getString("nombre"));
                m.setEstado(rs.getInt("estado"));
                lista.add(m);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Edificio> readEdificiosActivos() {
        List<Edificio> lista = new ArrayList<>();

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM EDIFICIO WHERE ESTADO = 1");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Edificio e = new Edificio();
                e.setId(rs.getInt("ID_EDIFICIO"));
                e.setNombre(rs.getString("NOMBRE"));
                e.setEstado(rs.getInt("ESTADO"));
                lista.add(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    public List<Edificio> readEdificio(){
        List<Edificio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO WHERE estado = 1 ORDER BY id_edificio ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Edificio m = new Edificio();
                m.setId(rs.getInt("id_edificio"));
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
    public boolean restaurarEdificio(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE EDIFICIO SET estado = 1 WHERE id_edificio = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, id);
            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Edificio> readEdificioEspecifico(String texto) {

        List<Edificio> lista = new ArrayList<>();
        try{
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT * FROM EDIFICIO WHERE id_edificio LIKE ? OR " +
                    "nombre LIKE ? OR " +
                    "estado LIKE ? ORDER BY id_edificio ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, "%"+texto+"%");
            ps.setString(2, "%"+texto+"%");
            ps.setString(3, "%"+texto+"%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                Edificio m = new Edificio();
                m.setId(rs.getInt("id_edificio"));
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
