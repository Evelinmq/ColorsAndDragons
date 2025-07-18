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
import javafx.scene.input.KeyCode;
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
            Parent root = loader.load();
            //Mandar Puesto a nueva vista
            UpdatePuestoController controller = loader.getController();
            controller.setP(p);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Puesto");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
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
    private void abrirVentanaAgregarPuesto() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/integradora/RegistrarPuesto.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Agregar Puesto");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana anterior hasta que se cierre esta
            stage.showAndWait();

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


}
