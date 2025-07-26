package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ResguardoBienDao {

    public boolean createResguardoBien(Resguardo rb) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "INSERT INTO RESGUARDO_BIEN (ESTADO) VALUES(?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, rb.getEstado());

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


    public boolean updateResguardoBien(int idResguardoBienViejo, Espacio m, Empleado e, Resguardo r) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE ESPACIO SET ID_ESPACIO = ?, NOMBRE = ?, ESTADO = ?, ID_EDIFICIO = ? WHERE ID_ESPACIO = ?";
            String queryDos = "UPDATE EMPLEADO SET NOMBRE = ?, APELLIDO_PATERNO = ?, APELLIDO_MATERNO = ?, ID_PUESTO = ?, ID_UNIDAD = ?, ESTADO = 1  WHERE RFC = ? ";
            String queryTres = "UPDATE RESGUARDO SET FECHA = ?, ESTADO = ? WHERE ID_RESGUARDO = ? ";
            PreparedStatement ps = conn.prepareStatement(query);
            PreparedStatement psdos = conn.prepareStatement(queryDos);
            PreparedStatement pstres = conn.prepareStatement(queryTres);
            ps.setInt(1, m.getId());
            ps.setString(2, m.getNombre());
            ps.setInt(3, m.getEstado());
            ps.setInt(4, m.getEdificio().getId());
            ps.setInt(5, idResguardoBienViejo);
            psdos.setString(1, e.getNombre());
            psdos.setString(2, e.getApellidoPaterno());
            psdos.setString(3, e.getApellidoMaterno());
            psdos.setInt(4, e.getIdPuesto());
            psdos.setInt(5, e.getIdUnidadAdministrativa());
            psdos.setInt(6, e.getEstado());
            pstres.setString(1, r.getFecha().toString());
            pstres.setInt(2, r.getEstado());

            if (ps.executeUpdate() > 0) {
                conn.close();
                return true;
            }
            conn.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return false;
    }

    public boolean deleteResguardoBien(int id) {
        try {
            Connection conn = OracleDatabaseConnectionManager.getConnection();
            String query = "UPDATE RESGUARDO_BIEN SET ESTADO = 0 WHERE ID_RESGUARDOBIEN = ?";
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

    //READ POR ESTADO


    //READ TODOS


    //READ SOLO UNO

    //READ RESGUARDO_BIEN ESPECÍFICO

}
