package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.dao.EspacioDao;
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

public class EspacioController implements Initializable {

    @FXML private TableView<Espacio> tabla;
    @FXML private TableColumn<Espacio, String> espacio;
    @FXML private TableColumn<Espacio, String> edificio;

    @FXML private TextField textoBusqueda;
    @FXML private Button botonBusqueda;
    @FXML private Button editar;
    @FXML private Button eliminar;

    private final EspacioDao dao = new EspacioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Cargar datos iniciales
        espacio.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        edificio.setCellValueFactory(new PropertyValueFactory<>("nombreEdificio"));

        recargarTabla();

        editar.setOnAction(event -> {
            Espacio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicion(seleccionado);
            } else {
                mostrarAlerta("Debes seleccionar un espacio para editar.");
            }
        });

        eliminar.setOnAction(event -> {
            Espacio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (dao.deleteEspacio(seleccionado.getId())) {
                        tabla.getItems().remove(seleccionado);
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un espacio para eliminar.");
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

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás segura de que deseas borrar el espacio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void abrirVentanaEdicion(Espacio espacioSeleccionado) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("VistaEspacio.fxml"));
            Parent root = loader.load();

            UpdateEspacioController controller = loader.getController();
            controller.setEspacio(espacioSeleccionado);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar espacio");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            recargarTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buscar(ActionEvent event) {
        botonBusqueda.setDisable(true);
        String texto = textoBusqueda.getText();

        Task<List<Espacio>> tareaBusqueda = new Task<>() {
            @Override
            protected List<Espacio> call() {
                return dao.readEspacioEspecifico(texto);
            }
        };

        tareaBusqueda.setOnSucceeded(e -> {
            botonBusqueda.setDisable(false);
            ObservableList<Espacio> resultados = FXCollections.observableList(tareaBusqueda.getValue());
            tabla.setItems(resultados);
            tabla.refresh();
        });

        tareaBusqueda.setOnFailed(e -> {
            botonBusqueda.setDisable(false);
            System.err.println("Error al buscar espacios: " + tareaBusqueda.getException());
        });

        new Thread(tareaBusqueda).start();
    }

    private void recargarTabla() {
        List<Espacio> espacios = dao.readEspacio();
        ObservableList<Espacio> lista = FXCollections.observableList(espacios);
        tabla.setItems(lista);
        tabla.refresh();
    }
}


