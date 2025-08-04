package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.UsuarioDao;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class UsuarioController implements Initializable {

    @FXML
    private TextField textoBusquedaUsuario;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private ComboBox<String> filtroEstado;
    @FXML
    private Button botonBusquedaUsuario, eliminarUsuario, actualizarUsuario, agregar, recuperar;
    @FXML
    private AnchorPane padreUsuario;
    @FXML
    private TableView<Usuario> tablaUsuario;
    @FXML
    private TableColumn<Usuario, String> tablaUsuarioCorreo;
    @FXML
    private TableColumn<Usuario, String> tablaUsuarioRol;
    @FXML
    private TableColumn<Usuario, String> tablaUsuarioContrasena;
    @FXML
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario, puesto;

    private UsuarioDao dao = new UsuarioDao();
    private List<Usuario> usuarios = new ArrayList<>();
    ObservableList<Usuario> opcionesTabla;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        List<Usuario> lista = dao.readUsuario();

        tablaUsuarioCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        tablaUsuarioRol.setCellValueFactory(new PropertyValueFactory<>("rolDescripcion"));
        tablaUsuarioContrasena.setCellValueFactory(new PropertyValueFactory<>("CONTRASENIA"));
        recargarTabla();

        ObservableList<Usuario> listaObservable = FXCollections.observableList(lista);
        tablaUsuario.setItems(listaObservable);

        tablaUsuario.setOnMouseClicked(click -> {
            if (tablaUsuario.getSelectionModel().getSelectedItem() != null) {
                eliminarUsuario.setDisable(true);
                actualizarUsuario.setDisable(true);
                recuperar.setDisable(false);
            } else {
                eliminarUsuario.setDisable(false);
                actualizarUsuario.setDisable(false);
                recuperar.setDisable(true);

            }
        });



        opcionesTabla = FXCollections.observableArrayList(usuarios);
        tablaUsuario.setItems(opcionesTabla);

        actualizarUsuario.setDisable(true);
        tablaUsuario.getSelectionModel().selectedItemProperty().addListener((obserable, oldValue, newValue) -> {
            actualizarUsuario.setDisable(newValue == null); //aquí cambie nuevo por newValue
        });

        actualizarUsuario.setOnAction(event -> {
            Usuario seleccion = tablaUsuario.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicionUsuario(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar un usuario para editar");
                alert.showAndWait();
            }
        });

        tablaUsuario.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            eliminarUsuario.setDisable(newValue == null);
        });

        eliminarUsuario.setOnAction(event -> {
            Usuario seleccionado = tablaUsuario.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (dao.deleteUsuario(seleccionado.getCorreo())) {
                        recargarTabla();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Éxito");
                        alert.setHeaderText(null);
                        alert.setContentText("El usuario ha sido eliminado correctamente.");
                        alert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un usuario para eliminar");
                alert.showAndWait();
                recargarTabla();
            }
        });

        tablaUsuario.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getEstado() == 0) {
                recuperar.setDisable(false);
            } else {
                recuperar.setDisable(true);
            }
        });

        recuperar.setOnAction(event -> {
            Usuario seleccionado = tablaUsuario.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRegresar()) {
                    if (UsuarioDao.regresoUsuario(seleccionado.getCorreo())) {
                        recargarTabla();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un usuario");
                alert.showAndWait();
                recargarTabla();
            }
        });

        usuarios = FXCollections.observableArrayList(opcionesTabla);
        tablaUsuario.setItems(listaObservable);

        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "VerTodos");
        filtroEstado.setItems(estados);

        filtroEstado.setOnAction(click -> {
            String estadoSeleccionado = filtroEstado.getSelectionModel().getSelectedItem();
            if ("Inactivos".equals(estadoSeleccionado)) {
                tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 0));
            } else if ("Activos".equals(estadoSeleccionado)) {
                tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 1));
            } else if ("VerTodos".equals(estadoSeleccionado)) {
                tablaUsuario.setItems(listaObservable);
            }
        });

        agregar.setOnAction(event -> abrirVentanaRegistro());

        textoBusquedaUsuario.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                recargarTabla();
            }
        });
    }

    @FXML
    public void eliminarSeleccion() {
        if(tablaUsuario.getSelectionModel().getSelectedItem() != null) {
            Usuario seleccionado = tablaUsuario.getSelectionModel().getSelectedItem();
            tablaUsuario.getItems().remove(seleccionado);
        }
        tablaUsuario.getSelectionModel().clearSelection();
        eliminarUsuario.setDisable(true);
    }
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    private void abrirVentanaEdicionUsuario(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarUsuario.fxml"));
            Parent root = loader.load();
            UpdateUsuarioController controller = loader.getController();
            if (u != null) {
                controller.setUsuario(u);
            }

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Usuario");
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                recargarTabla();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error al abrir ventana");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("No se pudo abrir la ventana de edición. Por favor, inténtalo de nuevo.");
            errorAlert.showAndWait();
        }
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarUsuario.fxml"));
            Parent root = loader.load();

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(e -> recargarTabla());
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Usuario");
            stage.initOwner(escenaPrincipal.getWindow());
            stage.show();

            RegistrarUsuarioController controller = loader.getController();
            controller.setStage(stage);
            controller.setOnUsuarioCreado(() -> recargarTabla());

            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private boolean confirmarRegresar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar recuperación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas recuperar el usuario?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar el registro?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    @FXML
    private void buscarUsuario(ActionEvent event) {
        botonBusquedaUsuario.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusquedaUsuario.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        Task<List<Usuario>> tarea = new Task<>() {
            @Override
            protected List<Usuario> call() {
                List<Usuario> lista = dao.readUsuario();
                return lista.stream().filter(u -> {
                    boolean coincideTexto = u.getCorreo().toLowerCase().contains(texto);
                    boolean coincideEstado = true;

                    if (filtro != null) {
                        switch (filtro) {
                            case "Activos": coincideEstado = u.getEstado() == 1; break;
                            case "Inactivos": coincideEstado = u.getEstado() == 0; break;
                            case "VerTodos": coincideEstado = true; break;
                        }
                    }

                    return coincideTexto && coincideEstado;
                }).toList();
            }

            @Override
            protected void succeeded() {
                tablaUsuario.setItems(FXCollections.observableArrayList(getValue()));
                tablaUsuario.refresh();
                spinner.setVisible(false);
                botonBusquedaUsuario.setDisable(false);
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                botonBusquedaUsuario.setDisable(false);
            }
        };

        Thread hilo = new Thread(tarea);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void recargarTabla() {
        List<Usuario> lista = dao.readUsuario();
        ObservableList<Usuario> listaObservable = FXCollections.observableArrayList(lista);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        if (filtro != null) {
            switch (filtro) {
                case "Activos":
                    tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 1));
                    break;
                case "Inactivos":
                    tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 0));
                    break;
                case "VerTodos":
                    tablaUsuario.setItems(listaObservable);
                    break;
                default:
                    tablaUsuario.setItems(listaObservable);
            }
        } else {
            tablaUsuario.setItems(listaObservable);
        }

        tablaUsuario.refresh();
    }

    @FXML
    protected void irResguardo() {
        cambiarVista("/com/example/integradora/VistaResguardo.fxml", resguardo);
    }

    @FXML
    protected void irBienes() {
        cambiarVista("/com/example/integradora/VistaBienes.fxml", bienes);
    }

    @FXML
    protected void irEmpleados() {
        cambiarVista("/com/example/integradora/VistaEmpleado.fxml", empleados);
    }

    @FXML
    protected void irEspacio() {
        cambiarVista("/com/example/integradora/VistaEspacio.fxml", espacio);
    }

    @FXML
    protected void irUnidad() {
        cambiarVista("/com/example/integradora/VistaUnidadAdm.fxml", unidad);
    }

    @FXML
    protected void irEdificio() {
        cambiarVista("/com/example/integradora/VistaEdificio.fxml", edificio);
    }

    @FXML
    protected void irUsuario() {
        cambiarVista("/com/example/integradora/VistaUsuario.fxml", usuario);
    }

    @FXML
    protected void irPuesto() {
        cambiarVista("/com/example/integradora/VistaPuesto.fxml", puesto);
    }


    private void cambiarVista(String ruta, Button boton) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(ruta));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) boton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
