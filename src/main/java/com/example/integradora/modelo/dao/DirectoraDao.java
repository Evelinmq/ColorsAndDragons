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

    public List<Directora> readDirectora(){
         String query = "SELECT * FROM directora ORDER BY ASC";


        List<Directora> directoras = new ArrayList<Directora>();
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){
                Directora d = new Directora();
                d.setIdResguardo(rs.getInt("IdResguardo"));
                d.setCodigoBien(rs.getString("CodigoBien"));
                d.setIdEspacio(rs.getInt("Espacio"));
                d.setIdEdificio(rs.getInt("Edificio"));
                d.setRfcEmpleado(rs.getString("RFCEmpleado"));
                d.setIdUnidad(rs.getInt("IDUnidad"));
                d.setIdPuesto(rs.getInt("IDPuesto"));
                directoras.add(d);
            }
            rs.close();
            conn.close();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return directoras;
    }
}
