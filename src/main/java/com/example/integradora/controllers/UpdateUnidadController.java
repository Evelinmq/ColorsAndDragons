package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateUnidadController implements Initializable {

    @FXML
    private TextField unidad;
    @FXML
    private Button actualizar;

    private UnidadAdministrativa u;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualizar.setOnAction((event) -> {
            UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
            dao.updateUnidadAdministrativa(null, unidad.getText());

            Stage ventana  = (Stage) id.getScene().getWindow();
            ventana.close();
        });
    }

    public void setUnidad(UnidadAdministrativa u) {
        this.u = u;
        this.idViejito = u.getId();

        unidad.setText(u.getNombre());
    }

}
