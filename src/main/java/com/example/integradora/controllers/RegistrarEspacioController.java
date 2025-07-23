package com.example.integradora.controllers;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.dao.EdificioDao;
import com.example.integradora.modelo.dao.EspacioDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class RegistrarEspacioController {

    @FXML private TextField nombreEspacio;
    @FXML private ComboBox<Edificio> comboEdificio;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;

    private final EspacioDao espacioDao = new EspacioDao();
    private final EdificioDao edificioDao = new EdificioDao();

    @FXML
    public void initialize() {
        cargarEdificios();

        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    private void cargarEdificios() {
        List<Edificio> edificios = edificioDao.readEdificiosActivos(); // usa solo edificios activos
        comboEdificio.setItems(FXCollections.observableArrayList(edificios));
    }

    private void guardar() {
        String nombre = nombreEspacio.getText().trim();
        Edificio edificioSeleccionado = comboEdificio.getValue();

        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debes ingresar un nombre.");
            return;
        }

        if (edificioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debes seleccionar un edificio.");
            return;
        }

        Espacio nuevo = new Espacio();
        nuevo.setNombre(nombre);
        nuevo.setEdificio(edificioSeleccionado);  // usa objeto Edificio, no ID
        nuevo.setEstado(1); // activo por defecto

        if (espacioDao.createEspacio(nuevo)) {
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo registrar el espacio.");
        }
    }

    private void cerrarVentana() {
        Stage ventana = (Stage) nombreEspacio.getScene().getWindow();
        ventana.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

