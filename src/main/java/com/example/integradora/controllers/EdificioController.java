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
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EdificioController implements Initializable {

    @FXML private TextField nombreEdificio;
    @FXML private TableView<Edificio> tabla;
    @FXML private TableColumn<Edificio, String> tablaEdificio;
    @FXML private TextField textoBusqueda;
    @FXML private Button botonBusqueda;
    @FXML private Button editar;
    @FXML private Button eliminar;
    @FXML private Button agregar;
    @FXML public TextField nombreEdi;
    @FXML public Button cancelar;
    @FXML public Button guardar;


    private EdificioDao dao = new EdificioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaEdificio.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        recargarTabla();

        // Activar botones cuando se selecciona un elemento
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editar.setDisable(!haySeleccion);
            eliminar.setDisable(!haySeleccion);
        });

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
                        recargarTabla();
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un edificio para eliminar.");
            }
        });

        // Botón agregar
        agregar.setOnAction(event -> abrirVentanaRegistro());
    }

    private void abrirVentanaEdicion(Edificio edificio) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditarEdificio.fxml"));
            Parent root = loader.load();

            UpdateEdificioController controller = loader.getController();
            controller.setEdificio(edificio); // Cargar datos del edificio seleccionado

            Scene escenaPrincipal = editar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Edificio");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            // Actualiza tabla al cerrar
            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                recargarTabla();
            });

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("RegistrarEdificio.fxml"));
            Parent root = loader.load();

            RegistrarEdificioController controller = loader.getController();

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Edificio");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            controller.btnGuardar.setOnAction(e -> {
                String nombre = controller.nombreEdificio.getText().trim();
                if (!nombre.isEmpty()) {
                    Edificio nuevo = new Edificio(nombre);
                    if (dao.createEdificio(nuevo)) {
                        recargarTabla();
                    }
                    stage.close();
                    fondo.setEffect(null);
                } else {
                    new Alert(Alert.AlertType.WARNING, "Debes ingresar un nombre.").showAndWait();
                }
            });

            controller.btnCancelar.setOnAction(e -> {
                stage.close();
                fondo.setEffect(null);
            });

            stage.show();
            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás segura de que deseas eliminar este edificio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public void buscar(ActionEvent event) {
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
            System.err.println("Error: " + cargarBusqueda.getException());
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



