package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioController {
    @FXML
    private TableView<Usuario> tablaUsuario;
    @FXML
    TableColumn<Usuario, String> tablaUsuarioCorreo;
    @FXML
    TableColumn<Usuario, String> tablaUsuarioContrasena;
    @FXML
    TableColumn<Usuario, String> tablaUsuarioRol;
    @FXML
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario;
    @FXML
    private Button botonBusquedaUsuario, eliminarUsuario, actualizarUsuario, agregar, recuperar;
    @FXML
    private TextField textoBusquedaUsuario;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private ComboBox<String> filtroEstado;
    @FXML
    private TextField correoUsuario, contraseniaUsuario, rolUsuario;
    @FXML
    private Button guardarUsuario;

    private List<Usuario> usuarios = new ArrayList<>();
    private UsuarioDao usuarioDAO = new UsuarioDao();
    private ObservableList<Usuario> listaObservable;

    @FXML
    private void initialize() {
        tablaUsuarioCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        tablaUsuarioContrasena.setCellValueFactory(new PropertyValueFactory<>("contrasena"));
        tablaUsuarioRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        recargarTabla();

        tablaUsuario.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            eliminarUsuario.setDisable(newVal == null);
            actualizarUsuario.setDisable(newVal == null);
        });

        eliminarUsuario.setDisable(true);
        actualizarUsuario.setDisable(true);
        recuperar.setDisable(true);

        eliminarUsuario.setOnAction(event -> {
            Usuario seleccionado = tablaUsuario.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (usuarioDAO.deleteUsuario(seleccionado.getCorreo())) {
                        recargarTabla();
                        mostrarAlerta("El usuario ha sido eliminado correctamente.");
                    }
                }
            }
        });

        textoBusquedaUsuario.textProperty().addListener((obs, old, newVal) -> {
            if (newVal.trim().isEmpty()) {
                recargarTabla();
            }
        });

        botonBusquedaUsuario.setOnAction(event -> buscarUsuario());

        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "VerTodos");
        filtroEstado.setItems(estados);

        filtroEstado.setOnAction(event -> {
            String estadoSeleccionado = filtroEstado.getSelectionModel().getSelectedItem();
            if ("Activos".equals(estadoSeleccionado)) {
                tablaUsuario.setItems(listaObservable.filtered(usuario -> usuario.getEstado() == 1));
            } else if ("Inactivos".equals(estadoSeleccionado)) {
                tablaUsuario.setItems(listaObservable.filtered(usuario -> usuario.getEstado() == 0));
            } else {
                tablaUsuario.setItems(listaObservable);
            }
        });

        guardarUsuario.setOnAction(event -> crearUsuario());

        agregar.setOnAction(event -> abrirVentanaRegistro());

        actualizarUsuario.setOnAction(event -> {
            Usuario seleccionado = tablaUsuario.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicion(seleccionado);
            }
        });
    }

    private void recargarTabla() {
        usuarios = usuarioDAO.readUsuarios();
        listaObservable = FXCollections.observableArrayList(usuarios);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        if (filtro != null) {
            switch (filtro) {
                case "Activos" -> tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 1));
                case "Inactivos" -> tablaUsuario.setItems(listaObservable.filtered(u -> u.getEstado() == 0));
                default -> tablaUsuario.setItems(listaObservable);
            }
        } else {
            tablaUsuario.setItems(listaObservable);
        }

        tablaUsuario.refresh();
    }

    private void buscarUsuario() {
        botonBusquedaUsuario.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusquedaUsuario.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        Task<List<Usuario>> tareaBusqueda = new Task<>() {
            @Override
            protected List<Usuario> call() {
                List<Usuario> lista = usuarioDAO.readUsuarios();
                return lista.stream()
                        .filter(u -> {
                            boolean coincideTexto = u.getCorreo().toLowerCase().contains(texto);
                            boolean coincideEstado = true;

                            if (filtro != null) {
                                switch (filtro) {
                                    case "Activos" -> coincideEstado = u.getEstado() == 1;
                                    case "Inactivos" -> coincideEstado = u.getEstado() == 0;
                                }
                            }

                            return coincideTexto && coincideEstado;
                        })
                        .toList();
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

        Thread hilo = new Thread(tareaBusqueda);
        hilo.setDaemon(true);
        hilo.start();
    }

    private void abrirVentanaEdicion(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarUsuario.fxml"));
            Parent root = loader.load();

            UpdateUsuarioController controller = loader.getController();
            controller.setUsuario(u);

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Usuario");
            stage.showAndWait();

            recargarTabla();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarUsuario.fxml"));
            Parent root = loader.load();

            RegistrarUsuarioController controller = loader.getController();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Usuario");

            controller.setStage(stage);
            controller.setOnUsuarioCreado(() -> recargarTabla());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar el usuario?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void crearUsuario() {
        String correo = correoUsuario.getText().trim();
        String contrasena = contraseniaUsuario.getText().trim();
        String rol = rolUsuario.getText().trim();

        if (correo.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasena);
        nuevo.setRol(rol);
        nuevo.setEstado(1);

        boolean creado = usuarioDAO.createUsuario(nuevo);

        if (creado) {
            correoUsuario.clear();
            contraseniaUsuario.clear();
            rolUsuario.clear();
            recargarTabla();
        } else {
            mostrarAlerta("No se pudo crear el usuario.");
        }
    }

    public void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
