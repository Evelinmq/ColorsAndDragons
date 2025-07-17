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

    private Bien bien;

    @FXML
    public void registrarBien(ActionEvent actionEvent) {

        String codigo = codigoBien.getText();
        String descripcion = descripcionBien.getText();
        String marca = marcaBien.getText();
        String modelo = modeloBien.getText();
        String serie = serieBien.getText();

        Bien bien = new Bien();

        BienDao dao = new BienDao();

        boolean exito = dao.bienCreate(bien);
        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exito registro");
            alert.setHeaderText("Bien creada");
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error en el registro");
            alert.setHeaderText("No se pudo registrar");

        }

    }


    public void setDialogStage(Stage stage) {
        
    }
}
