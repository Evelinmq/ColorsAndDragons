package com.example.integradora.controllers;


import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.controllers.PuestoController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class RegistrarPuestoController{

    @FXML
    private Button cancelarPuesto;
    @FXML
    private TextField labelPuesto;
    @FXML
    private Button guardar;

    private Stage stage;

    public void setDialogStage(Stage stage) {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void cerrarVentana() {
        if(stage != null) {
            stage.close();
        }
    }

    @FXML
    private void guardarPuesto() {
        String nombrePuesto = labelPuesto.getText();

        if(nombrePuesto.isEmpty() || nombrePuesto == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("EL campo no puede ir vacío");
            alerta.showAndWait();
            return;
        }
    }


    @FXML
    public void registrarPuesto(ActionEvent actionEvent) {
        // Obtenemos la info del campo de texto
        String nombreV = labelPuesto.getText().trim();
        if(nombreV.isEmpty() || nombreV == null) return;

        Puesto p = new Puesto();
        p.setNombre(nombreV);
        p.setEstado(1); // activo

        PuestoDao dao = new PuestoDao();



        boolean exito = dao.createPuesto(p);
        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exito registro");
            alert.setHeaderText("Se ha creado un nuevo puesto");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error en el registro");
            alert.setHeaderText("No se pudo registrar");

        }

        labelPuesto.setText("");

    }
}

