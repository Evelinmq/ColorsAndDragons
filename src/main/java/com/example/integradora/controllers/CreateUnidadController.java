package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.Optional;

public class CreateUnidadController {
    @FXML
    private TextField unidadNombre;
    @FXML
    private Button guardarUnidad;
    @FXML
    private Button cancelarUnidad;

    private UnidadAdministrativaDao unidadAdministrativaDao = new UnidadAdministrativaDao();

    @FXML
    private void initialize() {
        guardarUnidad.setOnAction(event -> {
        });
    }

    private void crearUnidad() {
        String nombre = unidadNombre.getText().trim();

        if (nombre.isEmpty()) {
            mostrarAlerta("El nombre de la Unidad Administrativa no puede ir vacío");
            return;
        }

        UnidadAdministrativa nuevaUnidad = new UnidadAdministrativa();
        nuevaUnidad.setNombre(nombre);

        boolean creado = unidadAdministrativaDao.createUnidadAdministrativa(nuevaUnidad);

        if (creado) {
            System.out.println("Unidad Administrativa creada exitosamente");
            unidadNombre.clear();
        } else {
            mostrarAlerta("El nombre de la Unidad Administrativa no puede ser creado");
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
