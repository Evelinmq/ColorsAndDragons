package com.example.integradora.controllers;

import com.example.integradora.utils.OracleDatabaseConnectionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class InformesController {
    @FXML
    private Label Inicio;

    @FXML
    protected void IrBienvenido() {
        try {
            // Cargar el archivo .jasper
            InputStream input = getClass().getResourceAsStream("/Resguardo.jasper");
            JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

            Connection conexion = OracleDatabaseConnectionManager.getConnection();

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("NOMBRE_ESPACIO", "Aula 1");
            parametros.put("FECHA_RESGUARDO", "2025-04-04");

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

            // Mostrar vista previa con JasperViewer (requiere Swing, pero funciona desde JavaFX)
            JasperViewer.viewReport(jasperPrint, false);
            JasperExportManager.exportReportToPdfFile(jasperPrint, "reporte.pdf");
            // O imprimir directamente (opcional)
            // JasperPrintManager.printReport(jasperPrint, true);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

