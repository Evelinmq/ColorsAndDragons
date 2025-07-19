package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
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


public class UnidadController implements Initializable {

    @FXML
    private TableView<UnidadAdministrativa> tablaUnidad;
    @FXML
    TableColumn<UnidadAdministrativa, String> tablaUnidadNombre;
    @FXML
    private AnchorPane padreUnidad;
    @FXML
    private Button resguardo, bienes, empleados, puesto, espacio, edificio, usuario;

    @FXML
    private TextField textoBusquedaUnidad;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Button botonBusquedaUnidad, eliminarUnidad, actualizarUnidad, agregarUnidad;
    @FXML
    private ComboBox<UnidadAdministrativa> filtroUnidad;

    private List<UnidadAdministrativa> unidades;

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        //Acceder BD
        unidades = new ArrayList<>();
        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
        unidades = dao.readUnidadAdministrativa();

        //Configuración de columnas
        tablaUnidadNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        //Lista observable
        tablaUnidad.setItems(FXCollections.observableList(unidades));



        //Cambiar opciones de editar y eliminar a reaccionar con botones
        eliminarUnidad.setOnAction(event -> {
            UnidadAdministrativa seleccionado = tablaUnidad.getSelectionModel().getSelectedItem();

            if (seleccionado != null && !tablaUnidad.getSelectionModel().isEmpty()) {
                int id = seleccionado.getId();

                boolean exito = new UnidadAdministrativaDao().deleteUnidadAdministrativa(id);

                if (exito) {
                    //eliminar de la lista observable o refrescar
                    tablaUnidad.getItems().remove(seleccionado);
                    //volver a cargar la lista desde la BD
                    System.out.println("Eliminación lógica exitosa.");
                } else {
                    System.out.println("No se pudo eliminar el puesto.");
                }
            } else {
                System.out.println("Selecciona un puesto primero.");
            }
        });

        actualizarUnidad.setOnAction(e -> {
            UnidadAdministrativa seleccionado = (UnidadAdministrativa) tablaUnidad.getSelectionModel().getSelectedItem();

            if (seleccionado != null && !tablaUnidad.getSelectionModel().isEmpty()) {
                abrirVentanaEdicionUnidad(seleccionado);
            }
            tablaUnidad.refresh();
        });

    }

    private void abrirVentanaEdicionUnidad(UnidadAdministrativa unidad) {
        //Cargar nueva vista
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarUnidad.fxml"));
            Scene scene = new Scene(loader.load());
            //Mandar Unidad a nueva vista
            UpdateUnidadController controller = loader.getController();
            //Sacar stage desde componente abierto
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Editar Unidad");
            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreUnidad.setEffect(blur);
            stage.showAndWait();
            padreUnidad.setEffect(null);
            UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
            unidades.clear();
            unidades.addAll(dao.readUnidadAdministrativa());
            tablaUnidad.setItems(FXCollections.observableList(unidades));
            tablaUnidad.refresh();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void agregarUnidad() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarUnidad.fxml"));
            Scene scene = new Scene(loader.load());
            RegistrarUnidadController controller = loader.getController();
            //Sacar el stage desde uno ya abierto
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior hasta que se cierre esta
            stage.setTitle("Agregar Unidad");
            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreUnidad.setEffect(blur);
            stage.showAndWait();
            padreUnidad.setEffect(null);
            UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
            unidades.clear();
            unidades.addAll(dao.readUnidadAdministrativa());
            tablaUnidad.setItems(FXCollections.observableList(unidades));
            tablaUnidad.refresh();
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
        botonBusquedaUnidad.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusquedaUnidad.getText();

        Task<List<UnidadAdministrativa>> cargarBusqueda = new Task<>(){
            @Override
            protected List<UnidadAdministrativa> call() throws Exception{
                UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
                List<UnidadAdministrativa> lista = dao.readUnidadAdministrativaEspecifico(texto);
                return lista;
            }
        };
        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusquedaUnidad.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Algo falló " + cargarBusqueda.getException());
        });
        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusquedaUnidad.setDisable(false);
            spinner.setVisible(false);
            List<UnidadAdministrativa> lista = cargarBusqueda.getValue();
            ObservableList<UnidadAdministrativa> listaObservable = FXCollections.observableList(lista);
            tablaUnidad.setItems(listaObservable);
            tablaUnidad.refresh();
        });
        Thread thread = new Thread(cargarBusqueda);
        thread.start();
    }

    @FXML
    protected void irResguardo (){
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
