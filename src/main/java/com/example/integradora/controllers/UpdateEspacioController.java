package com.example.integradora.controllers;

import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.dao.EspacioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateEspacioController implements Initializable {

    @FXML
    private MenuButton editarEdiEspacio;
    @FXML
    private TextField editarEspacio;

    private Espacio e;
    private int idViejito;
    private String nombreNuevo;

    private ObservableList<String> opcionesEspacio = FXCollections.observableArrayList(
            "Opción 1", "Opción 2", "Opción 3"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (String opcion : opcionesEspacio) {
            MenuItem item = new MenuItem(opcion);
            item.setOnAction(e -> {
                nombreNuevo = opcion;
                editarEdiEspacio.setText(opcion);
            });
            editarEdiEspacio.getItems().add(item);
        }
    }

    public void setEspacio(Espacio e) {
        this.e = e;
        this.idViejito = e.getId();

        editarEdiEspacio.setText(nombreNuevo);
        editarEspacio.setText(e.getNombre());
    }

    @FXML
    public void updateEspacio(ActionEvent event) {
        String ediEspacio = editarEspacio.getText();

        e.setId(idViejito);
        e.setNombre(ediEspacio);

        EspacioDao dao = new EspacioDao();

        if (dao.updateEspacio(idViejito, e)) {
            System.out.println("Espacio editado correctamente");
        } else {
            System.out.println("Error al actualizar espacio");
        }

        Stage ventana = (Stage) editarEspacio.getScene().getWindow();
        ventana.close();
    }
}



