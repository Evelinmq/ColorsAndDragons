package com.example.integradora.modelo.dao;

import com.example.integradora.modelo.Directora;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.utils.OracleDatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DirectoraDao {

    public List<Directora> readDirectora() {
        String sql = """
            SELECT r.ID_RESGUARDO, r.FECHA, r.ESTADO,
                   r.RFC_EMPLEADO,
                   e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO,
                   s.ID_ESPACIO, s.NOMBRE AS NOMBRE_ESPACIO
            FROM RESGUARDO r
            JOIN EMPLEADO e ON e.RFC = r.RFC_EMPLEADO
            JOIN ESPACIO  s ON s.ID_ESPACIO = r.ESPACIO_ID
            ORDER BY r.ID_RESGUARDO ASC
            """;

        List<Directora> lista = new ArrayList<>();

        try (Connection conn = OracleDatabaseConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Directora d = mapRow(rs);
                lista.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Directora> search(String filtro, String texto) {
        filtro = filtro == null ? "Todos" : filtro;
        texto  = texto == null ? "" : texto.trim();

        String base = """
            SELECT r.ID_RESGUARDO, r.FECHA, r.ESTADO,
                   r.RFC_EMPLEADO,
                   e.NOMBRE, e.APELLIDO_PATERNO, e.APELLIDO_MATERNO,
                   s.ID_ESPACIO, s.NOMBRE AS NOMBRE_ESPACIO
            FROM RESGUARDO r
            JOIN EMPLEADO e ON e.RFC = r.RFC_EMPLEADO
            JOIN ESPACIO  s ON s.ID_ESPACIO = r.ESPACIO_ID
            """;

        List<Directora> lista = new ArrayList<>();

        try (Connection conn = OracleDatabaseConnectionManager.getConnection()) {
            PreparedStatement ps;

            switch (filtro) {
                case "ID" -> {
                    base += " WHERE r.ID_RESGUARDO = ? ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    ps.setInt(1, Integer.parseInt(texto));
                }
                case "Fecha" -> {

                    LocalDate fecha = parseFecha(texto);
                    base += " WHERE TRUNC(r.FECHA) = ? ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    ps.setDate(1, Date.valueOf(fecha));
                }
                case "RFC" -> {
                    base += " WHERE UPPER(r.RFC_EMPLEADO) LIKE ? ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    ps.setString(1, "%" + texto.toUpperCase() + "%");
                }
                case "Empleado" -> {
                    base += " WHERE (UPPER(e.NOMBRE) LIKE ? OR UPPER(e.APELLIDO_PATERNO) LIKE ? OR UPPER(e.APELLIDO_MATERNO) LIKE ?) ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    String like = "%" + texto.toUpperCase() + "%";
                    ps.setString(1, like);
                    ps.setString(2, like);
                    ps.setString(3, like);
                }
                case "Espacio" -> {
                    base += " WHERE UPPER(s.NOMBRE) LIKE ? ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    ps.setString(1, "%" + texto.toUpperCase() + "%");
                }
                case "Estado" -> {
                    base += " WHERE r.ESTADO = ? ORDER BY r.ID_RESGUARDO ASC";
                    ps = conn.prepareStatement(base);
                    ps.setInt(1, Integer.parseInt(texto));
                }
                default -> {

                    base += """
                        WHERE UPPER(r.RFC_EMPLEADO) LIKE ?
                           OR UPPER(e.NOMBRE) LIKE ?
                           OR UPPER(e.APELLIDO_PATERNO) LIKE ?
                           OR UPPER(e.APELLIDO_MATERNO) LIKE ?
                           OR UPPER(s.NOMBRE) LIKE ?
                           OR TO_CHAR(r.ID_RESGUARDO) LIKE ?
                           OR TO_CHAR(r.ESTADO) LIKE ?
                        ORDER BY r.ID_RESGUARDO ASC
                        """;
                    ps = conn.prepareStatement(base);
                    String like = "%" + texto.toUpperCase() + "%";
                    ps.setString(1, like);
                    ps.setString(2, like);
                    ps.setString(3, like);
                    ps.setString(4, like);
                    ps.setString(5, like);
                    ps.setString(6, "%" + texto + "%");
                    ps.setString(7, "%" + texto + "%");
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private Directora mapRow(ResultSet rs) throws SQLException {
        Empleado emp = new Empleado();
        emp.setRfc(rs.getString("RFC_EMPLEADO"));
        emp.setNombre(rs.getString("NOMBRE"));
        emp.setApellidoPaterno(rs.getString("APELLIDO_PATERNO"));
        emp.setApellidoMaterno(rs.getString("APELLIDO_MATERNO"));

        Espacio esp = new Espacio();
        esp.setId(rs.getInt("ID_ESPACIO"));
        esp.setNombre(rs.getString("NOMBRE_ESPACIO"));

        Directora d = new Directora();
        d.setIdResguardo(rs.getInt("ID_RESGUARDO"));
        Date fechaSql = rs.getDate("FECHA");
        d.setFecha(fechaSql != null ? fechaSql.toLocalDate() : null);
        d.setEstado(rs.getInt("ESTADO"));
        d.setEmpleado(emp);
        d.setEspacio(esp);
        return d;
    }

    private LocalDate parseFecha(String s) {
        // Acepta dd/MM/yyyy y yyyy-MM-dd;
        s = s.trim();
        if (s.contains("/")) {
            String[] p = s.split("/");
            // dd/MM/yyyy
            int d = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            int y = Integer.parseInt(p[2]);
            return LocalDate.of(y, m, d);
        } else {
            // yyyy-MM-dd
            return LocalDate.parse(s);
        }
    }
}


