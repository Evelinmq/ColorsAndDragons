package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;

    @FXML
    private Button cerrarVentana, guardarPuesto;

    private Puesto puesto;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setPuesto(Puesto puesto) {
        this.puesto= puesto;
        this.idViejito = puesto.getId();
        nombrePuesto.setText(puesto.getNombre());
    }

    @FXML
    public void guardarPuesto(ActionEvent event) {
        String nombreNuevo = nombrePuesto.getText().trim();
        if (nombreNuevo.isEmpty()) return;

        this.puesto.setNombre(nombreNuevo);
        this.puesto.setEstado(1); // aseguramos que siga activo

        PuestoDao dao = new PuestoDao();
        if (dao.updatePuesto(idViejito, puesto)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Puesto actualizado");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo actualizar el puesto");
            alert.showAndWait();
        }

        Stage ventana = (Stage) nombrePuesto.getScene().getWindow();
        ventana.close();
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        // Aquí va la lógica para cerrar la ventana
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}

