package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CreatePuestoController {
    @FXML
    private TextField puestoNombre;
    @FXML
    private Button guardarPuesto;
    @FXML
    private Button cancelarPuesto;

    private PuestoDao puestoDAO = new PuestoDao();

    @FXML
    private void initialize() {
        guardarPuesto.setOnAction(event -> {
        });
    }

    private void crearPuesto() {
        String nombre = puestoNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("El nombre del Puesto no puede ir vacío");
            return;
        }

        Puesto nuevoPuesto = new Puesto();
        nuevoPuesto.setNombre(nombre);

        boolean creado = puestoDAO.createPuesto(nuevoPuesto);

        if (creado) {
            System.out.println("Puesto creado exitosamente");
            puestoNombre.clear();
        } else {
            mostrarAlerta("El nombre del Puesto no puede ser creado");
        }
    }

    public void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
    }



}
