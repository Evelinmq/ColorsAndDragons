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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EspacioController implements Initializable {

    @FXML
    private TableView<Espacio> tabla;
    @FXML
    private AnchorPane padreEspacio;
    @FXML
    private TableColumn<Espacio, String> tablaEspacio;
    @FXML
    private TableColumn<Espacio, String> tablaEdificio;
    @FXML
    private TextField textoBusqueda;
    @FXML
    private Button botonBusqueda;
    @FXML
    private Button editar;
    @FXML
    private Button eliminar;
    @FXML
    private Button agregar;
    @FXML
    private Button bienes, resguardo, puesto, empleados, unidad, edificio, usuario;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Button regresoEspacio;
    @FXML
    private ComboBox<String> comboEstado;

    private final EspacioDao dao = new EspacioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaEspacio.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tablaEdificio.setCellValueFactory(cellData -> {
            Espacio espacio = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    espacio.getEdificio() != null ? espacio.getEdificio().getNombre() : ""
            );
        });

        comboEstado.getItems().addAll("Activos", "Inactivos", "Ver todos");
        comboEstado.setValue("Ver todos");
        comboEstado.setOnAction(e -> filtrarPorEstado());

        filtrarPorEstado();

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            if (haySeleccion) {
                if (newSelection.getEstado() == 1) {
                    editar.setDisable(false);
                    eliminar.setDisable(false);
                    regresoEspacio.setDisable(true);
                } else {
                    editar.setDisable(true);
                    eliminar.setDisable(true);
                    regresoEspacio.setDisable(false);
                }
            } else {
                editar.setDisable(true);
                eliminar.setDisable(true);
                regresoEspacio.setDisable(true);
            }
        });

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
                        filtrarPorEstado();
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un espacio para eliminar.");
            }
        });

        regresoEspacio.setOnAction(event -> {
            Espacio seleccionado = tabla.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRecuperar()) {
                    if (dao.restaurarEspacio(seleccionado.getId())) {
                        mostrarAlerta("Espacio restaurado correctamente.");
                        filtrarPorEstado();
                    } else {
                        mostrarAlerta("Error al restaurar el espacio.");
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un espacio para restaurar.");
            }
        });

        agregar.setOnAction(event -> abrirVentanaRegistro());

        botonBusqueda.setOnAction(this::buscar);
    }

    private void abrirVentanaEdicion(Espacio espacio) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditarEspacio.fxml"));
            Parent root = loader.load();
            UpdateEspacioController controller = loader.getController();
            controller.setEspacio(espacio);

            Scene escenaPrincipal = editar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Espacio");
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
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("ReservarEspacio.fxml"));
            Parent root = loader.load();

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Espacio");
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
        alert.setContentText("¿Estás segura de que deseas eliminar este espacio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private boolean confirmarRecuperar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar restauración");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas restaurar este espacio?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    @FXML
    protected void irResguardo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) resguardo.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irBienes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) bienes.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) empleados.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) usuario.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) unidad.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) puesto.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) edificio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buscar(ActionEvent event) {
        botonBusqueda.setDisable(true);
        spinner.setVisible(true); // Mostrar el spinner
        String texto = textoBusqueda.getText().trim();

        Task<List<Espacio>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Espacio> call() {
                return dao.readEspacioEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false); // Ocultar el spinner
            System.err.println("Error: " + cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false); // Ocultar el spinner
            List<Espacio> lista = cargarBusqueda.getValue();
            ObservableList<Espacio> listaObservable = FXCollections.observableList(lista);
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
        List<Espacio> lista = new ArrayList<>();

        switch (opcion) {
            case "Activos":
                lista = dao.readEspacioPorEstado(1);
                break;
            case "Inactivos":
                lista = dao.readEspacioPorEstado(0);
                break;
            case "Ver todos":
                lista = dao.readTodosEspacios();
                break;
        }

        tabla.setItems(FXCollections.observableList(lista));
        tabla.refresh();
        regresoEspacio.setDisable(true);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Cierre de Sesión");
        alert.setHeaderText("Estás a punto de cerrar la sesión.");
        alert.setContentText("¿Estás seguro de que quieres cerrar la sesión?");


        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/IniciarSesion.fxml"));
                Parent newRoot = fxmlLoader.load();
                Stage stage = (Stage) usuario.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setRoot(newRoot);


                stage.setTitle("Iniciar Sesión");
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de carga");
                errorAlert.setHeaderText("Error al cargar la vista de inicio de sesión.");
                errorAlert.setContentText("No se pudo cargar la vista de inicio de sesión");
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }
}



