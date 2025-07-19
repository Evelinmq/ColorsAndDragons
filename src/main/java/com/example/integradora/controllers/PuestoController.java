package com.example.integradora.controllers;

import com.example.integradora.Main;
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
    private Button botonBusquedaPuesto, eliminarPuesto, actualizarPuesto, agregarPuesto;
    @FXML
    private ComboBox<Puesto> filtroPuesto;

    private List<Puesto> puestos;


    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        //Acceder BD
        puestos = new ArrayList<>();
        PuestoDao dao = new PuestoDao();
        puestos = dao.readPuestos();

        //Configuración de columnas
        tablaPuestoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        //Lista observable
        tablaPuesto.setItems(FXCollections.observableList(puestos));



        //Cambiar opciones de editar y eliminar a reaccionar con botones
        eliminarPuesto.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();

            if (seleccionado != null && !tablaPuesto.getSelectionModel().isEmpty()) {
                int id = seleccionado.getId();

                boolean exito = new PuestoDao().deletePuesto(id);

                if (exito) {
                    //eliminar de la lista observable o refrescar
                    tablaPuesto.getItems().remove(seleccionado);
                    //volver a cargar la lista desde la BD
                    System.out.println("Eliminación lógica exitosa.");
                } else {
                    System.out.println("No se pudo eliminar el puesto.");
                }
            } else {
                System.out.println("Selecciona un puesto primero.");
            }
        });

        actualizarPuesto.setOnAction(e -> {
            Puesto seleccionado = (Puesto) tablaPuesto.getSelectionModel().getSelectedItem();

            if (seleccionado != null && !tablaPuesto.getSelectionModel().isEmpty()) {
                abrirVentanaEdicionPuesto(seleccionado);
            }
            tablaPuesto.refresh();
        });

        //actualizarPuesto.setOnAction(this::createPuesto);
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
            puestos.addAll(dao.readPuestos());
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
            puestos.addAll(dao.readPuestos());
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


    public void buscarPuesto(ActionEvent event){

        //deshabilitar boton para dar tiempo a realizar búsqueda
        botonBusquedaPuesto.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusquedaPuesto.getText();

        Task<List<Puesto>> cargarBusqueda = new Task<>(){
            @Override
            protected List<Puesto> call() throws Exception{
                PuestoDao dao = new PuestoDao();
                List<Puesto> lista = dao.readPuestoEspecifico(texto);
                return lista;
            }
        };
        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusquedaPuesto.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Algo falló " + cargarBusqueda.getException());
        });
        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusquedaPuesto.setDisable(false);
            spinner.setVisible(false);
            List<Puesto> lista = cargarBusqueda.getValue();
            ObservableList<Puesto> listaObservable = FXCollections.observableList(lista);
            tablaPuesto.setItems(listaObservable);
            tablaPuesto.refresh();
        });
        Thread thread = new Thread(cargarBusqueda);
        thread.start();
    }

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
