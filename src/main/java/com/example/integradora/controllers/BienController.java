package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Puesto;
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
import java.util.Optional;
import java.util.ResourceBundle;

public class BienController implements Initializable {

    @FXML
    private AnchorPane padreBien;

    @FXML
    private Button editarBien, borrarBien;

    @FXML
    private TableColumn<Bien, String> tablaCodigo, tablaDescripcion, tablaMarca, tablaModelo, tablaSerie;

    private ObservableList<Bien> listaBienesObservable;
    @FXML
    private TableView<Bien> tablaBien;


    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


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

        editarBien.setDisable(true);

        tablaBien.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editarBien.setDisable(newValue == null);
        });

        //selecionar bien para editar
        editarBien.setOnAction(event -> {
            Bien seleccion = tablaBien.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicion(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar un bien para editar");
                alert.showAndWait();
            }
        });

        tablaBien.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            borrarBien.setDisable(newValue == null);
        });

        //SELECCIONAR PARA ELIMINAR
        borrarBien.setOnAction(event -> {
            Bien seleccionado = tablaBien.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (BienDao.deleteBien(seleccionado.getBien_codigo())) {
                        tablaBien.getItems().remove(seleccionado);
                        cargarBienes();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un bien para eliminar");
                alert.showAndWait();
                cargarBienes();
            }
        });

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
        }
    }



    private void cargarBienes() {
        BienDao dao = new BienDao();
        List<Bien> datosDesdeBD = dao.readBien();

        listaBienesObservable.clear();

        if (datosDesdeBD != null) {
            listaBienesObservable.addAll(datosDesdeBD);
        }

        tablaBien.refresh();
    }
    @FXML
    protected void abrirVentanaEdicion(Bien b) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/integradora/EditarBien.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Bien");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaBien.getScene().getWindow());


            UpdateBienController updateBienController = fxmlLoader.getController();
            updateBienController.setDialogStage(stage);
            updateBienController.setBien(b);

            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreBien.setEffect(blur);

            stage.showAndWait();
            padreBien.setEffect(null);


            cargarBienes();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean confirmarEliminar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar el registro?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }



}
