package com.example.integradora.controllers;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.dao.EdificioDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateEdificioController implements Initializable {

    @FXML private TextField nombreEdi;
    @FXML private Button cancelar;
    @FXML private Button guardar;

    private Edificio edificio;
    private int idViejo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        guardar.setOnAction(this::updateEdificio);
        cancelar.setOnAction(e -> cerrarVentana());
    }

    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
        this.idViejo = edificio.getId();
        nombreEdi.setText(edificio.getNombre());
    }

    @FXML
    public void updateEdificio(ActionEvent event) {
        String nombreNuevo = nombreEdi.getText().trim();
        if (nombreNuevo.isEmpty()) return;

        edificio.setNombre(nombreNuevo);
        edificio.setEstado(1);

        EdificioDao dao = new EdificioDao();
        if (dao.updateEdificio(idViejo, edificio)) {
            // Solo cerrar si se actualizó correctamente
            Stage ventana = (Stage) nombreEdi.getScene().getWindow();
            ventana.close();
        } else {
            // Mostrar error si falla
            new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el edificio.").showAndWait();
        }
    }


    private void cerrarVentana() {
        Stage ventana = (Stage) nombreEdi.getScene().getWindow();
        ventana.close();
    }
}


