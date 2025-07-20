package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
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

    @FXML
    private AnchorPane padreBien;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        BienDao dao = new BienDao();
        List<Bien> datos = dao.readBien();

        for (Bien a : datos) {
            System.out.println(a.getBien_codigo());
        }

        tablaCodigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        tablaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tablaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tablaSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));

        ObservableList<Bien> datosObservables = FXCollections.observableArrayList(datos);

        tablaBien.setItems(datosObservables);






    }

    @FXML
        protected void abrirVentanaRegistro(){

            try{
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/integradora/NuevoBien.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.setTitle("Registro Bien");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setTitle("Agregar Bien");
                BoxBlur blur = new BoxBlur(3, 3, 3);
                padreBien.setEffect(blur);
                stage.showAndWait();
                padreBien.setEffect(null);
            }catch(IOException e){
                e.printStackTrace();
            }

        }

    }


