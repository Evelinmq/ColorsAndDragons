package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateUnidadController implements Initializable {

    @FXML
    private TextField nombreUnidad;

    private UnidadAdministrativa unidad;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setUnidadAdministrativa(UnidadAdministrativa unidad) {
        this.unidad = unidad;
        this.idViejito = unidad.getId();
        nombreUnidad.setText(unidad.getNombre());
    }

    @FXML
    public void guardarUnidad(ActionEvent event) {
        String nombreNuevo = nombreUnidad.getText().trim();
        if (nombreNuevo.isEmpty()) return;

        this.unidad.setNombre(nombreNuevo);
        this.unidad.setEstado(1); // aseguramos que siga activo

        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
        if (dao.updateUnidad(idViejito, unidad)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Unidad actualizada");
            alert.showAndWait();
        }else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al editar unidad");
            alert.showAndWait();
        }

        Stage ventana = (Stage) nombreUnidad.getScene().getWindow();
        ventana.close();
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        // Aquí va la lógica para cerrar la ventana
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
