package com.example.integradora.controllers;

import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateUnidadController implements Initializable {

    @FXML
    private TextField unidad;

    private UnidadAdministrativa u;
    private int idViejito;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setUnidad(UnidadAdministrativa u) {
        this.u = u;
        this.idViejito = u.getId();

        unidad.setText(u.getNombre());
    }

    @FXML
    public void updateUnidad(ActionEvent event) {
        //Obtener información de TextField
        String unidadV = unidad.getText();

        //Colocar nueva info
        u.setId(idViejito);
        u.setNombre(unidadV);
        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();

        //Actualizar BD
        if(dao.updateUnidadAdministrativa(idViejito/*.intValue()*/, u)){
            System.out.println("Datos actualizados");
        }

        //Cerrar ventana
        Stage ventana = (Stage) unidad.getScene().getWindow();
        ventana.close();
    }

}
