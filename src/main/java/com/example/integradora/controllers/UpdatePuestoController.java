package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.EdificioDao;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;

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
            System.out.println("Puesto actualizado");
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

    /*
    @FXML
    private void guardarPuesto(ActionEvent event) {
        System.out.println("Guardando Puesto");
        nuevoNombrePuesto = nombrePuesto.getText().trim();

        if (nombrePuesto == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Error");
            alerta.setHeaderText(null);
            alerta.setContentText("El campo no puede ir vacío");
            alerta.showAndWait();
            return;
        }

        PuestoDao dao = new PuestoDao();
        Puesto nuevo = new Puesto();
        nuevo.setNombre(nuevoNombrePuesto);
        nuevo.setEstado(1);

        boolean exito = dao.createPuesto(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Actualización exitosa");
            alert.setHeaderText(null);
            alert.setContentText("Se ha actualizado el Puesto");
            alert.showAndWait();
            cerrarVentana(event); // cerrar si fue exitoso
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo registrar el puesto.");
            alert.showAndWait();
        }*/
        // Después de guardar, probablemente quieras cerrar la ventana
        // Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        // stage.close();
}

