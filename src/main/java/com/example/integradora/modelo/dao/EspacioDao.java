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

    public boolean createEspacio(Espacio m) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO ESPACIO(NOMBRE, ESTADO, ID_EDIFICIO) VALUES(?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, m.getNombre());
            ps.setInt(2, m.getEstado());
            ps.setInt(3, m.getEdificio().getId());

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


    public boolean updateEspacio(int idEspacioViejo, Espacio m) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET ID_ESPACIO = ?, NOMBRE = ?, ESTADO = ?, ID_EDIFICIO = ? WHERE ID_ESPACIO = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getEstado());
            ps.setInt(4, m.getEdificio().getId());
            ps.setInt(5, idEspacioViejo);
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

    public boolean deleteEspacio(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET ESTADO = 0 WHERE ID_ESPACIO = ?";
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

    public List<Espacio> readEspacioPorEstado(int estado) {
        List<Espacio> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.ID_ESPACIO, e.NOMBRE, e.ESTADO, ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio " +
                    "FROM ESPACIO e JOIN EDIFICIO ed ON e.ID_EDIFICIO = ed.ID_EDIFICIO WHERE e.ESTADO = ? ORDER BY e.ID_ESPACIO ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, estado);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("NOMBRE"));
                espacio.setEstado(rs.getInt("ESTADO"));
                espacio.setEdificio(edificio);

                lista.add(espacio);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Espacio> readTodosEspacios() {
        List<Espacio> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.ID_ESPACIO, e.NOMBRE, e.ESTADO, ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio " +
                    "FROM ESPACIO e JOIN EDIFICIO ed ON e.ID_EDIFICIO = ed.ID_EDIFICIO ORDER BY e.ID_ESPACIO ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("NOMBRE"));
                espacio.setEstado(rs.getInt("ESTADO"));
                espacio.setEdificio(edificio);

                lista.add(espacio);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Espacio> readEspacio() {
        return readEspacioPorEstado(1);
    }

    public boolean restaurarEspacio(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET ESTADO = 1 WHERE ID_ESPACIO = ?";
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

    public List<Espacio> readEspacioEspecifico(String texto) {
        List<Espacio> lista = new ArrayList<>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "SELECT e.ID_ESPACIO, e.NOMBRE, e.ESTADO, ed.ID_EDIFICIO, ed.NOMBRE AS nombre_edificio, ed.ESTADO AS estado_edificio " +
                    "FROM ESPACIO e JOIN EDIFICIO ed ON e.ID_EDIFICIO = ed.ID_EDIFICIO " +
                    "WHERE e.ID_ESPACIO LIKE ? OR e.NOMBRE LIKE ? OR e.ESTADO LIKE ? OR ed.ID_EDIFICIO LIKE ? " +
                    "ORDER BY e.ID_ESPACIO ASC";
            PreparedStatement ps = conn.prepareStatement(query);
            String search = "%" + texto + "%";
            ps.setString(1, search);
            ps.setString(2, search);
            ps.setString(3, search);
            ps.setString(4, search);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Edificio edificio = new Edificio();
                edificio.setId(rs.getInt("ID_EDIFICIO"));
                edificio.setNombre(rs.getString("nombre_edificio"));
                edificio.setEstado(rs.getInt("estado_edificio"));

                Espacio espacio = new Espacio();
                espacio.setId(rs.getInt("ID_ESPACIO"));
                espacio.setNombre(rs.getString("NOMBRE"));
                espacio.setEstado(rs.getInt("ESTADO"));
                espacio.setEdificio(edificio);

                lista.add(espacio);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}


