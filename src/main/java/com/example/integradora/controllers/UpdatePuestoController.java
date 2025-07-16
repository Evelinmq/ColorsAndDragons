package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField puesto;
    @FXML
    private Button actualizar;

    private Puesto p;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        actualizar.setOnAction(( event) -> {
            PuestoDao dao = new PuestoDao();
            dao.updatePuesto(idViejito, new Puesto(null, puesto.getText()));

            Stage ventana = (Stage) id.getScene().getWindow();
            ventana.close();
        });
    }

    public void setP(Puesto p) {
        this.p = p;
        //this.idViejito = p.getId();

        puesto.setText(p.getNombre());

    }

}
