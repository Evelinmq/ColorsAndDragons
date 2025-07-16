package com.example.integradora.controllers;

import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.dao.EdificioDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateEdificioController implements Initializable {

    @FXML
    private TextField nombreEdificio;

    private Edificio edificio;
    private int idViejo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setEdificio(Edificio edificio) {
        this.edificio = edificio;
        this.idViejo = edificio.getId();
        nombreEdificio.setText(edificio.getNombre());
    }

    @FXML
    public void updateEdificio(ActionEvent event) {

        String nombreNuevo = nombreEdificio.getText();

        // Actualizar objeto
        edificio.setId(idViejo);
        edificio.setNombre(nombreNuevo);

        EdificioDao dao = new EdificioDao();

        // Actualizar en base de datos
        if (dao.updateEdificio(idViejo, edificio)) {
            System.out.println("Edificio Actualizado");
        }

        // Cerrar ventana
        Stage ventana = (Stage) nombreEdificio.getScene().getWindow();
        ventana.close();
    }
}
