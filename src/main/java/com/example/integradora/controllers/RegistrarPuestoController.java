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

    @FXML public TextField labelPuesto;
    @FXML public Button btnCancelar;
    @FXML public Button btnGuardar;

    private Stage stage;

    public void setDialogStage(Stage stage) {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Runnable onPuestoCreado;

    public void setOnPuestoCreado(Runnable onPuestoCreado) {
        this.onPuestoCreado = onPuestoCreado;
    }

    @FXML
    private void guardarPuesto (ActionEvent event) {
        System.out.println("Guardando Puesto");
        String nombrePuesto = labelPuesto.getText().trim();

        if (nombrePuesto == null || nombrePuesto.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("El campo no puede ir vacío");
            alerta.showAndWait();
            return;
        }

        PuestoDao dao = new PuestoDao();
        Puesto nuevo = new Puesto();
        nuevo.setNombre(nombrePuesto);
        nuevo.setEstado(1);

        boolean exito = dao.createPuesto(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Se ha creado un nuevo puesto");
            alert.showAndWait();
            if (onPuestoCreado != null) {
                onPuestoCreado.run();
            }
            cerrarVentana(event); // cerrar si fue exitoso
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo registrar el puesto");
            alert.showAndWait();
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        if (stage != null) {
            stage.close();
        } else {
            // cerrar por el nodo raíz si no hay stage asignado
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

}

