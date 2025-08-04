package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.dao.BienDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BienController implements Initializable {

    @FXML
    private AnchorPane padreBien;

    @FXML
    private Button editarBien, borrarBien, regresoBien;

    @FXML
    private TableColumn<Bien, String> tablaCodigo, tablaDescripcion, tablaMarca, tablaModelo, tablaSerie;

    private ObservableList<Bien> listaBienesObservable;
    @FXML
    private TableView<Bien> tablaBien;

    @FXML
    private Button resguardo, puesto, empleados, espacio, unidad, edificio, usuario;

    @FXML
    private ProgressIndicator spinner;
    @FXML
    private Button botonBusqueda;
    @FXML
    private TextField textoBusqueda;

    @FXML private ComboBox<String> filtroEstado;


    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        listaBienesObservable = FXCollections.observableArrayList();

        tablaCodigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        tablaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tablaMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        tablaModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        tablaSerie.setCellValueFactory(new PropertyValueFactory<>("serie"));


        tablaBien.setItems(listaBienesObservable);


        cargarBienes();

        editarBien.setDisable(true);

        tablaBien.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editarBien.setDisable(newValue == null);
        });

        //selecionar bien para editar
        editarBien.setOnAction(event -> {
            Bien seleccion = tablaBien.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicion(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar un bien para editar");
                alert.showAndWait();
            }
        });

        tablaBien.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            borrarBien.setDisable(newValue == null);
        });

        //SELECCIONAR PARA ELIMINAR
        borrarBien.setOnAction(event -> {
            Bien seleccionado = tablaBien.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (BienDao.deleteBien(seleccionado.getBien_codigo())) {
                        tablaBien.getItems().remove(seleccionado);
                        cargarBienes();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un bien para eliminar");
                alert.showAndWait();
                cargarBienes();
            }
        });

        tablaBien.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getEstado() == 0) {
                regresoBien.setDisable(false);
                editarBien.setDisable(true);
                borrarBien.setDisable(true);
            } else {
                regresoBien.setDisable(true);
                editarBien.setDisable(false);
                borrarBien.setDisable(false);
            }
        });

        //Regresar bien a estado 1
        regresoBien.setOnAction(event -> {
            Bien seleccionado = tablaBien.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRegresar()) {
                    if (BienDao.regresoBien(seleccionado.getBien_codigo())) {
                        tablaBien.getItems().remove(seleccionado);
                        cargarBienes();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un bien");
                alert.showAndWait();
                cargarBienes();
            }
        });

        List<Bien> todosLosBienes = BienDao.readTodosBienes();

        listaBienesObservable = FXCollections.observableArrayList(todosLosBienes);
        tablaBien.setItems(listaBienesObservable);

        // 4. Configurar el ComboBox
        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "VerTodos");
        filtroEstado.setItems(estados);

        filtroEstado.setOnAction(click -> {
            String estadoSeleccionado = filtroEstado.getSelectionModel().getSelectedItem();

            if ("Inactivos".equals(estadoSeleccionado)) {
                tablaBien.setItems(listaBienesObservable.filtered(bien -> bien.getEstado() == 0));
            } else if ("Activos".equals(estadoSeleccionado)) {
                tablaBien.setItems(listaBienesObservable.filtered(bien -> bien.getEstado() == 1));
            } else if ("VerTodos".equals(estadoSeleccionado)) {
            tablaBien.setItems(listaBienesObservable);
        }
        });

    }

    @FXML
    protected void abrirVentanaRegistro() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/integradora/NuevoBien.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Registro Bien");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaBien.getScene().getWindow());

            RegistroBienController registroController = fxmlLoader.getController();
            registroController.setDialogStage(stage);

            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreBien.setEffect(blur);

            stage.showAndWait();
            padreBien.setEffect(null);


            cargarBienes();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void cargarBienes() {
        BienDao dao = new BienDao();
        List<Bien> datosDesdeBD = dao.readBien();

        listaBienesObservable.clear();

        if (datosDesdeBD != null) {
            listaBienesObservable.addAll(datosDesdeBD);
        }

        tablaBien.refresh();
    }
    @FXML
    protected void abrirVentanaEdicion(Bien b) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/integradora/EditarBien.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Bien");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(tablaBien.getScene().getWindow());


            UpdateBienController updateBienController = fxmlLoader.getController();
            updateBienController.setDialogStage(stage);
            updateBienController.setBien(b);

            BoxBlur blur = new BoxBlur(3, 3, 3);
            padreBien.setEffect(blur);

            stage.showAndWait();
            padreBien.setEffect(null);


            cargarBienes();

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
    private boolean confirmarRegresar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar regresar");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas regresar el bien?");
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
    protected void irEdificio(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
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

            Stage stage = (Stage) espacio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void Buscar (ActionEvent event){


        botonBusqueda.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusqueda.getText();

        Task<List<Bien>> cargarBusqueda = new Task<>(){

            @Override
            protected List<Bien> call() throws Exception {
                BienDao dao = new BienDao();
                List<Bien> lista= dao.readBienEspecifico(texto);
                return lista;
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Algo fallo" +  cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBusqueda.setDisable(false);
            spinner.setVisible(false);
            List<Bien> lista = cargarBusqueda.getValue();
            ObservableList<Bien> listaObservable = FXCollections.observableList(lista);
            tablaBien.setItems(listaObservable);
            tablaBien.refresh();
        });


        Thread thread = new Thread(cargarBusqueda);
        thread.start();

    }





}
