package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Directora;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DirectoraDao {


    public List<Directora> readDirectora() {
        String query = "SELECT * FROM RESGUARDO ORDER BY ID_RESGUARDO ASC";

        List<Directora> directoras = new ArrayList<Directora>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Directora d = new Directora();
                d.setIdResguardo(rs.getInt("ID_RESGUARDO"));
                d.setCodigoBien(rs.getString("FECHA"));
                d.setIdEspacio(rs.getInt("ESPACIO_ID"));
                d.setRfcEmpleado(rs.getString("RFC_EMPLEADO"));
                directoras.add(d);
            }
            rs.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return directoras;
    }
}

