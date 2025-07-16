package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.UnidadAdministrativa;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import oracle.ucp.util.Task;

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
                abrirVentanaEdicionPuesto(seleccionado);
            }
            tablaUnidad.refresh();
        });

        actualizarUnidad.setOnAction(this::registrarPuesto);
    }

    private void abrirVentanaEdicionPuesto(UnidadAdministrativa u){
        //Cargar nueva vista
        try{
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarUnidad.fxml"));
            Parent root = loader.load();
            //Mandar Unidad a nueva vista
            UpdateUnidadController controller = loader.getController();
            controller.setUnidad(u);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Unidad Administrativa");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
            unidades.clear();
            unidades.addAll(dao.readUnidadAdministrativa());
            tablaUnidad.setItems(FXCollections.observableList(unidades));
            tablaUnidad.refresh();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //Crear el método abrir ventana edición y crear nueva clase de java para esto
    private void abrirVentanaAgregarPuesto(UnidadAdministrativa u){}

    public void registrarPuesto(ActionEvent event){
        //Obtener información de los textfield
        String nombre = nombreUnidad.getText();
        UnidadAdministrativa unidadAdministrativa = new UnidadAdministrativa(null, nombre, 1);
        UnidadAdministrativaDao dao = new UnidadAdministrativaDao();
        boolean exito = dao.createUnidadAdministrativa(unidadAdministrativa);
        unidades.clear();
        unidades.addAll(dao.readUnidadAdministrativa());
        tablaUnidad.setItems(FXCollections.observableList(unidades));
        tablaUnidad.refresh();
        if(dao.createUnidadAdministrativa(unidadAdministrativa)){
            System.out.println("Registro exitoso");
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
        cargarBusqueda.setOnSucceded(workerStateEvent -> {
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


}
