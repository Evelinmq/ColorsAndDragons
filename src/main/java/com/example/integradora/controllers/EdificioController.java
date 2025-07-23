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
    @FXML private Button regresoEdificio;
    @FXML private ComboBox<String> comboEstado;

    private final EdificioDao dao = new EdificioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaEdificio.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        comboEstado.getItems().addAll("Activos", "Inactivos", "Todos");
        comboEstado.setValue("Activos");
        comboEstado.setOnAction(e -> filtrarPorEstado());

        filtrarPorEstado();

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editar.setDisable(!haySeleccion);
            eliminar.setDisable(!haySeleccion);


            String estadoSeleccionado = comboEstado.getValue();
            regresoEdificio.setDisable(!haySeleccion || !"Inactivos".equals(estadoSeleccionado));
        });

        editar.setOnAction(event -> {
            Edificio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicion(seleccionado);
            } else {
                mostrarAlerta("Debes seleccionar un edificio para editar.");
            }
        });

        eliminar.setOnAction(event -> {
            Edificio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (dao.deleteEdificio(seleccionado.getId())) {
                        filtrarPorEstado();
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un edificio para eliminar.");
            }
        });

        regresoEdificio.setOnAction(event -> {
            Edificio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRecuperar()) {
                    if (dao.restaurarEdificio(seleccionado.getId())) {
                        mostrarAlerta("Edificio restaurado correctamente.");
                        filtrarPorEstado();
                    } else {
                        mostrarAlerta("Error al restaurar el edificio.");
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un edificio para restaurar.");
            }
        });

        agregar.setOnAction(event -> abrirVentanaRegistro());

        botonBusqueda.setOnAction(this::buscar);
    }

    private void abrirVentanaEdicion(Edificio edificio) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditarEdificio.fxml"));
            Parent root = loader.load();
            UpdateEdificioController controller = loader.getController();
            controller.setEdificio(edificio);

            Scene escenaPrincipal = editar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Edificio");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                filtrarPorEstado();
            });

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("RegistrarEdificio.fxml"));
            Parent root = loader.load();

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Edificio");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                filtrarPorEstado();
            });

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
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

    private boolean confirmarRecuperar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar restauración");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas restaurar este edificio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    public void buscar(ActionEvent event) {
        botonBusqueda.setDisable(true);
        String texto = textoBusqueda.getText().trim();

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

    @FXML
    private void filtrarPorEstado() {
        String opcion = comboEstado.getValue();
        List<Edificio> lista = new ArrayList<>();

        switch (opcion) {
            case "Activos":
                lista = dao.readEdificioPorEstado(1);
                break;
            case "Inactivos":
                lista = dao.readEdificioPorEstado(0);
                break;
            case "Todos":
                lista = dao.readTodosEdificios();
                break;
        }

        tabla.setItems(FXCollections.observableList(lista));
        tabla.refresh();

        // Deshabilitar botón de restaurar al cambiar el filtro
        regresoEdificio.setDisable(true);
    }
}






