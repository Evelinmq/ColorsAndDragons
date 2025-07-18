package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField puesto;
    @FXML
    private MenuButton editarPuesto;

    private Puesto p;
    private int idViejito;
    private String nuevoNombrePuesto;

    private ObservableList<String> opcionesPuesto = FXCollections.observableArrayList(
            "Opción 1", "Opción 2", "Opción 3"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (String opcion : opcionesPuesto) {
            MenuItem item = new MenuItem(opcion);
            item.setOnAction(e -> {
                nuevoNombrePuesto = opcion;
                editarPuesto.setText(opcion);
            });
            editarPuesto.getItems().add(item);
        }
    }

    public void setEspacio(Puesto p) {
        this.p = p;
        this.idViejito = p.getId();

        editarPuesto.setText(nuevoNombrePuesto);
        editarPuesto.setText(p.getNombre());
    }

    @FXML
    public void updatePuesto(ActionEvent event) {
        String ediPuesto = editarPuesto.getText();

        p.setId(idViejito);
        p.setNombre(ediPuesto);

        PuestoDao dao = new PuestoDao();

        if (dao.updatePuesto(idViejito, p)) {
            System.out.println("Puesto editado correctamente");
        } else {
            System.out.println("Error al actualizar el Puesto");
        }

        Stage ventana = (Stage) editarPuesto.getScene().getWindow();
        ventana.close();
    }
}
