package com.example.integradora.controllers;

import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdatePuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;

    private Puesto p;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setP(Puesto p) {
        this.p = p;
        this.idViejito = p.getId();

        nombrePuesto.setText(p.getNombre());

    }

    @FXML
    public void updatePuesto(ActionEvent event) {
        //Obtener información de los TextField
        String puestoV = nombrePuesto.getText();

        //Colocar nueva info
        p.setId(idViejito);
        p.setNombre(puestoV);
        PuestoDao dao = new PuestoDao();

        //Actualizar BD
        if(dao.updatePuesto(idViejito/*.intValue()*/, p)){
            System.out.println("Puesto Actualizado");
        }

        //Cerrar ventana
        Stage ventana = (Stage) nombrePuesto.getScene().getWindow();
        ventana.close();

    }


}
