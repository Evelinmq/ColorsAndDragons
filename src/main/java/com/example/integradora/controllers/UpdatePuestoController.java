package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.EdificioDao;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;
    @FXML
    private Button editarPuesto;

    private Puesto puesto;
    private int idViejito;
    private String nuevoNombrePuesto;

    private Stage stage;

    public void setDialogStage(Stage stage) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public void setPuesto(Puesto puesto) {
        this.puesto= puesto;
        this.idViejito = puesto.getId();
        nombrePuesto.setText(puesto.getNombre());
    }

    public void setEspacio(Puesto p) {
        this.puesto = p;
        this.idViejito = p.getId();

        editarPuesto.setText(nuevoNombrePuesto);
        editarPuesto.setText(p.getNombre());
    }

    @FXML
    public void updatePuesto(ActionEvent event) {
        String nombreNuevo = nombrePuesto.getText().trim();
        if (nuevoNombrePuesto.isEmpty()) return;

        puesto.setNombre(nombreNuevo);
        puesto.setEstado(1); // aseguramos que siga activo

        PuestoDao dao = new PuestoDao();
        if (dao.updatePuesto(idViejito, puesto)) {
            System.out.println("Puesto actualizado");
        }

        Stage ventana = (Stage) nombrePuesto.getScene().getWindow();
        ventana.close();
    }
}
