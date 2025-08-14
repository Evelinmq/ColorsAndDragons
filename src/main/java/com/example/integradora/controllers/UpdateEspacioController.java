package com.example.integradora.controllers;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.dao.EdificioDao;
import com.example.integradora.modelo.dao.EspacioDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateEspacioController implements Initializable {

    @FXML private TextField nombreEsp;
    @FXML private ComboBox<Edificio> comboEdificio;
    @FXML private Button cancelar;
    @FXML private Button btnGuardar;

    private Espacio espacio;
    private int idViejo;

    private final EspacioDao espacioDao = new EspacioDao();
    private final EdificioDao edificioDao = new EdificioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnGuardar.setOnAction(this::updateEspacio);
        cancelar.setOnAction(e -> cerrarVentana());
        cargarEdificios();
    }

    private void cargarEdificios() {
        List<Edificio> edificios = edificioDao.readEdificiosActivos();
        comboEdificio.setItems(FXCollections.observableArrayList(edificios));
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
        this.idViejo = espacio.getId();
        nombreEsp.setText(espacio.getNombre());
        comboEdificio.setValue(espacio.getEdificio());
    }

    @FXML
    public void updateEspacio(ActionEvent event) {
        String nuevoNombre = nombreEsp.getText().trim();
        Edificio edificioSeleccionado = comboEdificio.getValue();

        if (nuevoNombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debes ingresar un nombre.");
            return;
        }

        if (edificioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Debes seleccionar un edificio.");
            return;
        }

        espacio.setNombre(nuevoNombre);
        espacio.setEdificio(edificioSeleccionado);
        espacio.setEstado(1); // se mantiene activo al actualizar

        if (espacioDao.updateEspacio(idViejo, espacio)) {
            cerrarVentana();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "No se pudo actualizar el espacio.");
        }
    }

    private void cerrarVentana() {
        Stage ventana = (Stage) nombreEsp.getScene().getWindow();
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




