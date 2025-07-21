package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroBienController {

    @FXML
    private TextField codigoBien, descripcionBien, marcaBien, modeloBien, serieBien;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @FXML
    public void registrarBien(ActionEvent actionEvent) {

        String codigoV = codigoBien.getText().trim();
        String descripcionV = descripcionBien.getText().trim();
        String marcaV = marcaBien.getText().trim();
        String modeloV = modeloBien.getText().trim();
        String serieV = serieBien.getText().trim();


        if (codigoV.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Validación");
            alert.setHeaderText("Campo Obligatorio Vacío");
            alert.setContentText("El campo 'Código' no puede estar vacío. Por favor, ingrese un valor.");
            alert.showAndWait();
            return;
        }


        Bien b = new Bien();
        b.setBien_codigo(codigoV);
        b.setDescripcion(descripcionV);
        b.setMarca(marcaV);
        b.setModelo(modeloV);
        b.setSerie(serieV);
        b.setEstado(1);

        BienDao dao = new BienDao();
        boolean exito = dao.bienCreate(b);


        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito en el Registro");
            alert.setHeaderText("Bien Registrado Correctamente");
            alert.setContentText("El nuevo bien ha sido guardado en el sistema.");
            alert.showAndWait(); // Muestra la alerta de éxito

            codigoBien.setText("");
            descripcionBien.setText("");
            marcaBien.setText("");
            modeloBien.setText("");
            serieBien.setText("");

            if (dialogStage != null) {
                dialogStage.close();
            }

        } else {
            // Error en el registro
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en el Registro");
            alert.setHeaderText("No se pudo registrar el bien");
            alert.setContentText("Ocurrió un problema al guardar el bien en la base de datos. Por favor, inténtelo de nuevo.");
            alert.showAndWait();
        }
    }

    @FXML
    public void cancelarBien(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}