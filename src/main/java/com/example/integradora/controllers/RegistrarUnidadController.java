package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrarUnidadController {

    @FXML
    private Button cancelar;
    @FXML
    private TextField labelUnidad;
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
    private void guardarUnidad() {
        String nombreUnidad = labelUnidad.getText();

        if(nombreUnidad.isEmpty() || nombreUnidad == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("EL campo no puede ir vacío");
            alerta.showAndWait();
            return;
        }
    }


    @FXML
    public void registrarUnidad(ActionEvent actionEvent) {

        String nombre = labelUnidad.getText();

        UnidadAdministrativa u = new UnidadAdministrativa();

        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();

        boolean exito = dao.createUnidadAdministrativa(u);
        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exito registro");
            alert.setHeaderText("Se ha creado una nueva unidad administrativa");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error en el registro");
            alert.setHeaderText("No se pudo registrar");

        }

    }
}
