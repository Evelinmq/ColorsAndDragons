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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EdificioController implements Initializable {

    @FXML private TextField nombreEdificio;
    @FXML private TableView<Edificio> tabla;
    @FXML
    private AnchorPane padreEdificio;
    @FXML private TableColumn<Edificio, String> tablaEdificio;
    @FXML private TextField textoBusqueda;
    @FXML private Button botonBusqueda;
    @FXML private Button editar;
    @FXML private Button eliminar;
    @FXML private Button agregar;
    @FXML private Button regresoEdificio;
    @FXML
    private Button bienes,resguardo, puesto, empleados, espacio, unidad, usuario;
    @FXML
    private ProgressIndicator spinner;
    @FXML private ComboBox<String> comboEstado;

    private final EdificioDao dao = new EdificioDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaEdificio.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        comboEstado.getItems().addAll("Activos", "Inactivos", "Ver todos");
        comboEstado.setValue("Activos");
        comboEstado.setOnAction(e -> filtrarPorEstado());

        filtrarPorEstado();

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;

            if (haySeleccion) {
                if (newSelection.getEstado() == 1) {
                    editar.setDisable(false);
                    eliminar.setDisable(false);
                    regresoEdificio.setDisable(true);
                } else {
                    editar.setDisable(true);
                    eliminar.setDisable(true);
                    regresoEdificio.setDisable(false);
                }
            } else {
                editar.setDisable(true);
                eliminar.setDisable(true);
                regresoEdificio.setDisable(true);
            }
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
        alert.setContentText("¿Estás seguro de que deseas eliminar este edificio?");
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
    @FXML
    protected void irResguardo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) resguardo.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irBienes(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) bienes.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) empleados.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) usuario.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) unidad.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) puesto.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) espacio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void buscar(ActionEvent event) {
        botonBusqueda.setDisable(true);
        spinner.setVisible(true); // Mostrar spinner
        String texto = textoBusqueda.getText().trim();

        Task<List<Edificio>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Edificio> call() {
                return dao.readEdificioEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Error: " + cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false);
            List<Edificio> lista = cargarBusqueda.getValue();
            ObservableList<Edificio> listaObservable = FXCollections.observableList(lista);
            tabla.setItems(listaObservable);
            tabla.refresh();
        });

        new Thread(cargarBusqueda).start();
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
            case "Ver todos":
                lista = dao.readTodosEdificios();
                break;
        }

        tabla.setItems(FXCollections.observableList(lista));
        tabla.refresh();

        // Deshabilitar botón de restaurar al cambiar el filtro
        regresoEdificio.setDisable(true);
    }
}






