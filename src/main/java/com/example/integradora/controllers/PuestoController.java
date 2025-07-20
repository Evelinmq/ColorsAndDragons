package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class PuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;
    @FXML
    private TableView<Puesto> tablaPuesto;
    @FXML
    TableColumn<Puesto, String> tablaPuestoNombre;
    @FXML
    private AnchorPane padrePuesto;
    @FXML
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario;

    @FXML
    private TextField textoBusquedaPuesto;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Button botonBusquedaPuesto, eliminarPuesto, actualizarPuesto, agregar, recuperar;
    @FXML
    private ComboBox<Puesto> filtroPuesto;

    private List<Puesto> puestos = new ArrayList<>();

    private PuestoDao dao = new PuestoDao();


    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        // 1. Acceder a la BD
        List<Puesto> lista = dao.readPuesto();

        //Configuración columa
        tablaPuestoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        //Lista observable
        ObservableList<Puesto> listaObservable = FXCollections.observableList(lista);
        tablaPuesto.setItems(listaObservable);


        // Botón eliminar
        eliminarPuesto.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (dao.deletePuesto(seleccionado.getId())) {
                        tablaPuesto.getItems().remove(seleccionado);
                    }
                }
            } else {
                mostrarAlerta("Debes seleccionar un puesto para eliminar.");
            }
        });

        // Botón editar
        actualizarPuesto.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicionPuesto(seleccionado);
            } else {
                mostrarAlerta("Debes seleccionar un puesto para editar.");
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

    private void abrirVentanaEdicionPuesto(Puesto p){
        //Cargar nueva vista
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarPuesto.fxml"));
            Scene scene = new Scene(loader.load());
            //Mandar Puesto a nueva vista
            UpdatePuestoController controller = loader.getController();
            //Sacar stage desde componente abierto
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Puesto");
            BoxBlur blur = new BoxBlur(3, 3, 3);
            padrePuesto.setEffect(blur);
            stage.showAndWait();
            padrePuesto.setEffect(null);
            PuestoDao dao = new PuestoDao();
            puestos.clear();
            puestos.addAll(dao.readPuesto());
            tablaPuesto.setItems(FXCollections.observableList(puestos));
            tablaPuesto.refresh();
        }catch (IOException e){
            e.printStackTrace();
        }
    }



    @FXML
    protected void agregarPuesto() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarPuesto.fxml"));
            Scene scene = new Scene(loader.load());
            RegistrarPuestoController controller = loader.getController();
            //Sacar el stage desde uno ya abierto
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior hasta que se cierre esta
            stage.setTitle("Agregar Puesto");
            BoxBlur blur = new BoxBlur(3, 3, 3);
            padrePuesto.setEffect(blur);
            stage.showAndWait();
            padrePuesto.setEffect(null);
            PuestoDao dao = new PuestoDao();
            puestos.clear();
            puestos.addAll(dao.readPuesto());
            tablaPuesto.setItems(FXCollections.observableList(puestos));
            tablaPuesto.refresh();
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


    public void buscarPuesto(ActionEvent event) {
        // Desactivar botón mientras busca
        botonBusquedaPuesto.setDisable(true);
        String texto = textoBusquedaPuesto.getText();

        Task<List<Puesto>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Puesto> call() {
                return dao.readPuestoEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusquedaPuesto.setDisable(false);
            System.err.println("Algo falló: " + cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusquedaPuesto.setDisable(false);
            List<Puesto> lista = cargarBusqueda.getValue();
            ObservableList<Puesto> listaObservable = FXCollections.observableList(lista);
            tablaPuesto.setItems(listaObservable);
            tablaPuesto.refresh();
        });

        Thread thread = new Thread(cargarBusqueda);
        thread.setDaemon(true);
        thread.start();
    }


    //Botones cambiar a vistas
    @FXML
    protected void irResguardo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
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
            //Sacar la stage desde un componente visual ya abieto
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
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) empleados.getScene().getWindow();
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

    @FXML
    protected void irUnidad(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) unidad.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) edificio.getScene().getWindow();
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

}
