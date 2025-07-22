package com.example.integradora.controllers;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.dao.EdificioDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrarEdificioController {

    @FXML private TextField nombreEdificio;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private final EdificioDao dao = new EdificioDao();

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    private void guardar() {
        String nombre = nombreEdificio.getText().trim();
        if (nombre.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Debes ingresar un nombre.").showAndWait();
            return;
        }

        Edificio nuevo = new Edificio(nombre);
        dao.createEdificio(nuevo);
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage ventana = (Stage) nombreEdificio.getScene().getWindow();
        ventana.close();
    }
}

