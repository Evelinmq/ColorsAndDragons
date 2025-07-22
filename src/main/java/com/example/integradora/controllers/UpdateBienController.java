package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UpdateBienController {

    @FXML
    private Button editarBien;

    @FXML
    private TextField editarCodigo, editarDescripcion, editarMarca, editarModelo, editarSerie;

    private Bien bien;
    private String Bien_Codigo, Descripcion, Marca, Modelo, Serie;
    private String codigoViejo;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    //EDITAR EL BIEN


    public void setBien(Bien bien) {
        this.bien= bien;
        this.Bien_Codigo = bien.getBien_codigo();
        this.Descripcion = bien.getDescripcion();
        this.Marca = bien.getMarca();
        this.Modelo = bien.getModelo();
        this.Serie = bien.getSerie();
        this.codigoViejo = bien.getBien_codigo();

        editarCodigo.setText(bien.getBien_codigo());
        editarDescripcion.setText(bien.getDescripcion());
        editarMarca.setText(bien.getMarca());
        editarModelo.setText(bien.getModelo());
        editarSerie.setText(bien.getSerie());


    }

    @FXML
    public void editarBien(ActionEvent event) {
        String codigoNuevo = editarCodigo.getText().trim();
        String descripcionNuevo = editarDescripcion.getText().trim();
        String marcaNuevo = editarMarca.getText().trim();
        String modeloNuevo = editarModelo.getText().trim();
        String serieNuevo = editarSerie.getText().trim();

        bien.setBien_codigo(codigoNuevo);
        bien.setDescripcion(descripcionNuevo);
        bien.setMarca(marcaNuevo);
        bien.setModelo(modeloNuevo);
        bien.setSerie(serieNuevo);
        bien.setEstado(1);

        BienDao dao = new BienDao();
        if (dao.updateBien(codigoViejo, bien)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SE ACTUALIZO!");
            alert.setContentText("Actualizado con exito");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de actualizar");
            alert.setContentText("No se logro actualizar con exito");
            alert.showAndWait();
        }

        if (dialogStage != null) {
            dialogStage.close();
        } else {
            Stage ventana = (Stage) editarCodigo.getScene().getWindow();
            ventana.close();
        }
    }

    @FXML
    public void cancelarEdicion(ActionEvent event) {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }



}