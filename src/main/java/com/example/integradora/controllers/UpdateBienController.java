package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    private ObservableList<Bien> listaBienesObservable;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        listaBienesObservable = FXCollections.observableArrayList();

        tablaCodigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        tablaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tablaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tablaSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));


        tablaBien.setItems(listaBienesObservable);


        cargarBienes();
    }

    @FXML
    protected void abrirVentanaRegistro() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/integradora/NuevoBien.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Registro Bien");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaBien.getScene().getWindow());

            RegistroBienController registroController = fxmlLoader.getController();
            registroController.setDialogStage(stage);

            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreBien.setEffect(blur);

            stage.showAndWait();
            padreBien.setEffect(null);


            cargarBienes();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Apertura");
            alert.setHeaderText("No se pudo abrir la ventana de registro de bienes.");
            alert.setContentText("Verifica la ruta del FXML o el controlador.");
            alert.showAndWait();
        }
    }


    private void cargarBienes() {
        BienDao dao = new BienDao();
        List<Bien> datosDesdeBD = dao.readBien();
        if (listaBienesObservable != null) {
            listaBienesObservable.clear();
        } else {

            listaBienesObservable = FXCollections.observableArrayList();
            tablaBien.setItems(listaBienesObservable);
        }

        if (datosDesdeBD != null) {
            listaBienesObservable.addAll(datosDesdeBD);
        }

        tablaBien.refresh();
    }
}