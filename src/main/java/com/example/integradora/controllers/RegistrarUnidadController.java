package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrarUnidadController {
    @FXML public TextField labelUnidad;
    @FXML public Button btnCancelar;
    @FXML public Button btnGuardar;

    private Stage stage;

    public void setDialogStage(Stage stage) {

    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Runnable onUnidadCreado;

    public void setOnUnidadCreado(Runnable onUnidadCreado) {
        this.onUnidadCreado = onUnidadCreado;
    }

    @FXML
    private void guardarUnidad (ActionEvent event) {
        String nombreUnidad = labelUnidad.getText().trim();

        if (nombreUnidad == null || nombreUnidad.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("El campo no puede ir vacío");
            alerta.showAndWait();
            return;
        }

        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
        UnidadAdministrativa nuevo = new UnidadAdministrativa();
        nuevo.setNombre(nombreUnidad);
        nuevo.setEstado(1);

        boolean exito = dao.createUnidad(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Se ha creado una nueva unidad administrativa");
            alert.showAndWait();
            if (onUnidadCreado != null) {
                onUnidadCreado.run();
            }
            cerrarVentana(event); // cerrar si fue exitoso
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo registrar la unidad administrativa");
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
