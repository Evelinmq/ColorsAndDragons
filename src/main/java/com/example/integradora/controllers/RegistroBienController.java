package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.BienDao;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistroBienController {

    @FXML
    private TextField codigoBien, descripcionBien, marcaBien, modeloBien, serieBien;

    private Bien bien;



    @FXML
    public void registrarBien(ActionEvent actionEvent) {


        String codigo = codigoBien.getText();
        String descripcion = descripcionBien.getText();
        String marca = marcaBien.getText();
        String modelo = modeloBien.getText();
        String serie = serieBien.getText();

        if (codigo == null || codigo.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("Este campo no puede estar vacio");
            return;
        }

        Bien b = new Bien();
        b.setBien_codigo(codigo);
        b.setDescripcion(descripcion);
        b.setMarca(marca);
        b.setModelo(modelo);
        b.setSerie(serie);
        b.setEstado(1);

        BienDao dao = new BienDao();

        boolean exito = dao.bienCreate(b);
            if (exito) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito registro");
                alert.setHeaderText("Se ha creado un nuevo bien");
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error en el registro");
                alert.setHeaderText("No se pudo registrar");

            }
        codigoBien.setText("");
        descripcionBien.setText("");
        marcaBien.setText("");
        modeloBien.setText("");
        serieBien.setText("");

        }



}
