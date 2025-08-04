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
    private ComboBox<String> filtroEstado;

    private List<Puesto> puestos = new ArrayList<>();

    private PuestoDao dao = new PuestoDao();

    ObservableList<Puesto> opcionesTabla;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        // 1. Acceder a la BD
        List<Puesto> lista = dao.readPuesto();


        //Configuración columa
        tablaPuestoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        //tablaPuesto.setItems(FXCollections.observableList(lista));
        recargarTabla();

        //Lista observable
        ObservableList<Puesto> listaObservable = FXCollections.observableList(lista);
        tablaPuesto.setItems(listaObservable);


        //Habilitar botón eliminar, editar, actualizar
        tablaPuesto.setOnMouseClicked(click -> {
            if(tablaPuesto.getSelectionModel().getSelectedItem() != null && tablaPuesto.getSelectionModel().getSelectedItem().getEstado() == 0) {
                //Activa botón
                eliminarPuesto.setDisable(true);
                actualizarPuesto.setDisable(true);
                recuperar.setDisable(false);

            }else{
                eliminarPuesto.setDisable(false);
                actualizarPuesto.setDisable(false);
                recuperar.setDisable(true);
            }
        });

        //Comienza código búsqueda
        opcionesTabla = FXCollections.observableArrayList(puestos);
        tablaPuesto.setItems(opcionesTabla);

        //Finaliza código búsqueda

        // Botón editar
        actualizarPuesto.setDisable(true);

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarPuesto.setDisable(newValue == null);
        });

        //selecionar puesto para editar
        actualizarPuesto.setOnAction(event -> {
            Puesto seleccion = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicionPuesto(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar un puesto para editar");
                alert.showAndWait();
            }
        });

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            eliminarPuesto.setDisable(newValue == null);
        });


        //SELECCIONAR PARA ELIMINAR
        eliminarPuesto.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (PuestoDao.deletePuesto(seleccionado.getId())) {
                        recargarTabla();

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Éxito");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("El puesto ha sido eliminado correctamente.");
                        successAlert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un puesto para eliminar");
                alert.showAndWait();
                recargarTabla();
            }
        });

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getEstado() == 0) {
                recuperar.setDisable(false);
            } else {
                recuperar.setDisable(true);
            }
        });

        //Regresar puesto a estado 1
        recuperar.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRegresar()) {
                    if (PuestoDao.regresoPuesto(seleccionado.getId())) {
                        recargarTabla();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un puesto");
                alert.showAndWait();
                recargarTabla();
            }
        });


        puestos = FXCollections.observableArrayList(opcionesTabla);
        tablaPuesto.setItems(listaObservable);

        // 4. Configurar el ComboBox
        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "VerTodos");
        filtroEstado.setItems(estados);

        filtroEstado.setOnAction(click -> {
            String estadoSeleccionado = filtroEstado.getSelectionModel().getSelectedItem();

            if ("Inactivos".equals(estadoSeleccionado)) {
                tablaPuesto.setItems(listaObservable.filtered(puesto -> puesto.getEstado() == 0));
            } else if ("Activos".equals(estadoSeleccionado)) {
                tablaPuesto.setItems(listaObservable.filtered(puesto -> puesto.getEstado() == 1));
            } else if ("VerTodos".equals(estadoSeleccionado)) {
                tablaPuesto.setItems(listaObservable);
            }
        });


    // Botón agregar
        agregar.setOnAction(event -> abrirVentanaRegistro());


        textoBusquedaPuesto.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                recargarTabla(); // Usa el metodo que respeta el filtro
            }
        });

    }

    @FXML
    public void eliminarSeleccion() {
        if(tablaPuesto.getSelectionModel().getSelectedItem() != null) {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            tablaPuesto.getItems().remove(seleccionado);
        }
        tablaPuesto.getSelectionModel().clearSelection();
        eliminarPuesto.setDisable(true);
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
            Parent root = loader.load();
            UpdatePuestoController controller = loader.getController();

            if (p != null) {
                controller.setPuesto(p); // Llama al setPuesto del controlador de edición
            }

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Puesto");
            stage.initOwner(escenaPrincipal.getWindow());
            //stage.show();

            stage.setOnHidden(e -> {
                fondo.setEffect(null); // Quita el blur
                recargarTabla();      // Recarga la tabla
            });

            stage.show();
            //stage.setOnHidden(e -> fondo.setEffect(null));
        }catch (IOException e){
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
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarPuesto.fxml"));
            Parent root = loader.load();

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(e -> recargarTabla());
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Puesto");
            stage.initOwner(escenaPrincipal.getWindow());
            stage.show();


            RegistrarPuestoController controller = loader.getController();
            controller.setStage(stage);
            controller.setOnPuestoCreado(() -> {
                recargarTabla();
            });


            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarPuesto(ActionEvent event) {
        // Obtenemos la info del campo de texto
        String puestoV = nombrePuesto.getText().trim();
        if (puestoV.isEmpty()) return;

        Puesto nuevo = new Puesto();
        nuevo.setNombre(puestoV);
        nuevo.setEstado(1); // activo

        if (dao.createPuesto(nuevo)) {
            System.out.println("Se insertó con éxito");
        }

        nombrePuesto.setText("");
        recargarTabla();
    }

    private boolean confirmarRegresar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar regresar");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas regresar el puesto?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private boolean confirmarEliminar(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar el registro?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    @FXML
    private void filtrarPorEstado() {
        String opcion = filtroEstado.getValue();
        List<Puesto> lista = new ArrayList<>();

        switch (opcion) {
            case "Activos":
                lista = dao.readPuestoPorEstado(1);
                break;
            case "Inactivos":
                lista = dao.readPuestoPorEstado(0);
                break;
            case "VerTodos":
                lista = dao.readTodosPuestos();
                break;
        }

        tablaPuesto.setItems(FXCollections.observableList(lista));
        tablaPuesto.refresh();

        // Deshabilitar botón de restaurar al cambiar el filtro
        recuperar.setDisable(true);
    }


    @FXML
    private void buscarPuesto(ActionEvent event) {
        // Desactiva el botón y muestra el spinner
        botonBusquedaPuesto.setDisable(true);
        spinner.setVisible(true);

        // Captura texto de búsqueda
        String texto = textoBusquedaPuesto.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        // Simula búsqueda en segundo plano
        Task<List<Puesto>> tareaBusqueda = new Task<>() {
            @Override
            protected List<Puesto> call() {
                List<Puesto> lista = dao.readPuesto(); // Carga todos los puestos
                return lista.stream()
                        .filter(p -> {
                            boolean coincideTexto = p.getNombre().toLowerCase().contains(texto);
                            boolean coincideEstado = true;

                            if (filtro != null) {
                                switch (filtro) {
                                    case "Activos": coincideEstado = p.getEstado() == 1; break;
                                    case "Inactivos": coincideEstado = p.getEstado() == 0; break;
                                    case "VerTodos": coincideEstado = true; break;
                                }
                            }

                            return coincideTexto && coincideEstado;
                        })
                        .toList();
            }

            @Override
            protected void succeeded() {
                List<Puesto> resultado = getValue();
                tablaPuesto.setItems(FXCollections.observableArrayList(resultado));
                tablaPuesto.refresh();

                spinner.setVisible(false);
                botonBusquedaPuesto.setDisable(false);
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                botonBusquedaPuesto.setDisable(false);
            }
        };

        Thread hilo = new Thread(tareaBusqueda);
        hilo.setDaemon(true);
        hilo.start();
    }


    private void recargarTabla() {
        List<Puesto> lista = dao.readPuesto();
        ObservableList<Puesto> listaObservable = FXCollections.observableArrayList(lista);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        if (filtro != null) {
            switch (filtro) {
                case "Activos":
                    tablaPuesto.setItems(listaObservable.filtered(p -> p.getEstado() == 1));
                    break;
                case "Inactivos":
                    tablaPuesto.setItems(listaObservable.filtered(p -> p.getEstado() == 0));
                    break;
                case "VerTodos":
                    tablaPuesto.setItems(listaObservable);
                    break;
                default:
                    tablaPuesto.setItems(listaObservable); // fallback
            }
        } else {
            tablaPuesto.setItems(listaObservable); // si no hay filtro seleccionado aún
        }

        tablaPuesto.refresh();
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
