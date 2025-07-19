package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
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

public class UpdateUnidadController implements Initializable {

    @FXML
    private TextField unidad;
    @FXML
    private MenuButton editarUnidad;

    private UnidadAdministrativa u;
    private int idViejito;
    private String nuevoNombreUnidad;

    private Stage stage;

    public void setDialogStage(Stage stage) {

    }

    private ObservableList<String> opcionesUnidad = FXCollections.observableArrayList(
            "Opción 1", "Opción 2", "Opción 3"
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (String opcion : opcionesUnidad) {
            MenuItem item = new MenuItem(opcion);
            item.setOnAction(e -> {
                nuevoNombreUnidad = opcion;
                editarUnidad.setText(opcion);
            });
            editarUnidad.getItems().add(item);
        }
    }

    public void setEspacio(UnidadAdministrativa u) {
        this.u = u;
        this.idViejito = u.getId();

        editarUnidad.setText(nuevoNombreUnidad);
        editarUnidad.setText(u.getNombre());
    }

    @FXML
    public void updateUnidad(ActionEvent event) {
        String ediUnidad = editarUnidad.getText();

        u.setId(idViejito);
        u.setNombre(ediUnidad);

        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();

        if (dao.updateUnidadAdministrativa(idViejito, u)) {
            System.out.println("Unidad Administrativa editada correctamente");
        } else {
            System.out.println("Error al actualizar la Unidad Administrativa");
        }

        Stage ventana = (Stage) editarUnidad.getScene().getWindow();
        ventana.close();
    }
}
