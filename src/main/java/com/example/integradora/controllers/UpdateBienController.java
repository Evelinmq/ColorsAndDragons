package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateBienController implements Initializable {

   @FXML
   private Button Registrar;
    @FXML
    private TableView<Bien> tablaBien;
    @FXML
    private TableColumn<Bien, String> tablaCodigo, tablaDescripcion, tablaMarca, tablaModelo, tablaSerie;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        BienDao dao = new BienDao();
        List<Bien> datos = dao.readBien();

        for (Bien a : datos) {
            System.out.println(a.getCodigo());
        }

        tablaCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        tablaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tablaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tablaSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));

        ObservableList<Bien> datosObservables = FXCollections.observableArrayList(datos);

        tablaBien.setItems(datosObservables);






    }

    @FXML
        private void abrirVentanaRegistro(ActionEvent event){

            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("NuevoBien.fxml"));
                Parent root = loader.load();
                RegistroBienController controller = loader.getController();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Registro Bien");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                controller.setDialogStage(stage);

                tablaBien.refresh();
            }catch(IOException e){
                e.printStackTrace();
            }

        }

    }
