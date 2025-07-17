package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.dao.EdificioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EdificioController implements Initializable {

    @FXML
    private TextField nombreEdificio;
    @FXML
    private TableView<Edificio> tabla;
    @FXML
    private TableColumn<Edificio, String> tablaEdificio;
    @FXML
    private TextField textoBusqueda;
    @FXML
    private Button botonBusqueda;
    @FXML
    private Button editar;
    @FXML
    private Button eliminar;

    private EdificioDao dao = new EdificioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Acceder a la BD
        List<Edificio> lista = dao.readEdificio();
        tablaEdificio.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        ObservableList<Edificio> listaObservable = FXCollections.observableList(lista);
        tabla.setItems(listaObservable);

        // Botón editar
        editar.setOnAction(event -> {
            Edificio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicion(seleccionado);
            } else {
                mostrarAlerta("Debes seleccionar un edificio para editar.");
            }
        });


        // Botón eliminar
        eliminar.setOnAction(event -> {
            Edificio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (dao.deleteEdificio(seleccionado.getId())) {
                        tabla.getItems().remove(seleccionado);
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un edificio para eliminar.");
            }
        });
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVentanaEdicion(Edificio m) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("VistaEdificio.fxml"));
            Parent root = loader.load();

            UpdateEdificioController controller = loader.getController();
            controller.setEdificio(m);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar edificio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            recargarTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarEdificio(ActionEvent event) {
        // Obtenemos la info del campo de texto
        String edificioV = nombreEdificio.getText().trim();
        if (edificioV.isEmpty()) return;

        Edificio nuevo = new Edificio();
        nuevo.setNombre(edificioV);
        nuevo.setEstado(1); // activo

        if (dao.createEdificio(nuevo)) {
            System.out.println("Se insertó con éxito");
        }

        nombreEdificio.setText("");
        recargarTabla();
    }

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás ABSOLUTAMENTE segura que deseas borrar el edificio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public void buscar(ActionEvent event) {
        // Desactivar botón mientras busca
        botonBusqueda.setDisable(true);
        String texto = textoBusqueda.getText();

        Task<List<Edificio>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Edificio> call() {
                return dao.readEdificioEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            System.err.println("Algo falló: " + cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            List<Edificio> lista = cargarBusqueda.getValue();
            ObservableList<Edificio> listaObservable = FXCollections.observableList(lista);
            tabla.setItems(listaObservable);
            tabla.refresh();
        });

        Thread thread = new Thread(cargarBusqueda);
        thread.setDaemon(true);
        thread.start();
    }

    private void recargarTabla() {
        List<Edificio> lista = dao.readEdificio();
        ObservableList<Edificio> listaObservable = FXCollections.observableList(lista);
        tabla.setItems(listaObservable);
        tabla.refresh();
    }
}

