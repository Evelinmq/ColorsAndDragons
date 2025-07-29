package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.dao.EmpleadoDao;
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
import java.util.stream.Collectors;

public class EmpleadoController implements Initializable {

    @FXML
    private TableView<Empleado> tablaEmpleado;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoNombre;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoApellidoPaterno;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoApellidoMaterno;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoRFC;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoPuesto;
    @FXML
    TableColumn<Empleado, String> tablaEmpleadoUnidadAdministrativa;
    @FXML
    private AnchorPane padreEmpleado;
    @FXML
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario, puesto;

    @FXML
    private TextField textoBusquedaEmpleado;
    @FXML
    private ProgressIndicator spinner;

    @FXML
    private Button botonBusquedaEmpleado, eliminarEmpleado, actualizarEmpleado, agregar, recuperar;
    @FXML
    private ComboBox<String> filtroEstado;

    private List<Empleado> empleadosList = new ArrayList<>();
    private EmpleadoDao dao = new EmpleadoDao();
    ObservableList<Empleado> opcionesTabla;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tablaEmpleadoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        tablaEmpleadoApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        tablaEmpleadoApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        tablaEmpleadoRFC.setCellValueFactory(new PropertyValueFactory<>("rfc"));
        tablaEmpleadoPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        tablaEmpleadoUnidadAdministrativa.setCellValueFactory(new PropertyValueFactory<>("unidadAdministrativa"));

        recargarTabla();

        actualizarEmpleado.setDisable(true);
        eliminarEmpleado.setDisable(true);
        recuperar.setDisable(true);

        tablaEmpleado.getSelectionModel().selectedItemProperty().addListener((obs, old, newValue) -> {
            actualizarEmpleado.setDisable(newValue == null);
            eliminarEmpleado.setDisable(newValue == null);
            recuperar.setDisable(newValue == null || newValue.getEstado() != 0);
        });

        tablaEmpleado.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tablaEmpleado.getSelectionModel().isEmpty()) {
                abrirVentanaEdicionEmpleado(tablaEmpleado.getSelectionModel().getSelectedItem());
            }
        });

        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "VerTodos");
        filtroEstado.setItems(estados);


        agregar.setOnAction(event -> abrirVentanaRegistro());
        actualizarEmpleado.setOnAction(event -> abrirVentanaEdicionEmpleado(tablaEmpleado.getSelectionModel().getSelectedItem()));
        eliminarEmpleado.setOnAction(event -> eliminarEmpleado());


        textoBusquedaEmpleado.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) recargarTabla();
        });
    }

    private void eliminarEmpleado() {
        Empleado seleccionado = tablaEmpleado.getSelectionModel().getSelectedItem();
        if (seleccionado != null && confirmarAccion("¿Deseas eliminar el empleado?")) {
            if (EmpleadoDao.deleteEmpleado(seleccionado.getRfc())) {
                recargarTabla();
            }
        }
    }

    private boolean confirmarAccion(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(EmpleadoController.class.getResource("/com/example/integradora/EditarEmpleados.fxml"));
            Parent root = loader.load();

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(e -> recargarTabla());
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Empleado");
            stage.initOwner(escenaPrincipal.getWindow());
            stage.show();

            RegistrarEmpleadoController controller = loader.getController();
            controller.setStage(stage);
            controller.setOnEmpleadoCreado(() -> recargarTabla());

            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaEdicionEmpleado(Empleado e) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarEmpleado.fxml"));
            Parent root = loader.load();
            UpdateEmpleadoController controller = loader.getController();

            if (e != null) {
                controller.setEmpleado(e);
            }

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Empleado");
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(ev -> {
                fondo.setEffect(null);
                recargarTabla();
            });

            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void recargarTabla() {
        List<Empleado> lista = dao.readEmpleados();
        if(lista == null || lista.isEmpty()){

            return;
        }

        ObservableList<Empleado> listaObservable = FXCollections.observableArrayList(lista);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();
        if (filtro != null) {
            switch (filtro) {
                case "Activos" -> tablaEmpleado.setItems(listaObservable.filtered(e -> e.getEstado() == 1));
                case "Inactivos" -> tablaEmpleado.setItems(listaObservable.filtered(e -> e.getEstado() == 0));
                case "VerTodos" -> tablaEmpleado.setItems(listaObservable);
                default -> tablaEmpleado.setItems(listaObservable);
            }
        } else {
            tablaEmpleado.setItems(listaObservable);
        }

        tablaEmpleado.refresh();
    }

    @FXML
    private void buscarEmpleado(ActionEvent event) {
        botonBusquedaEmpleado.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusquedaEmpleado.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        Task<List<Empleado>> tarea = new Task<>() {
            @Override
            protected List<Empleado> call() {
                List<Empleado> lista = dao.readEmpleados();
                return lista.stream()
                        .filter(e -> {
                            boolean coincideTexto = e.getNombre().toLowerCase().contains(texto);
                            boolean coincideEstado = true;
                            if (filtro != null) {
                                coincideEstado = switch (filtro) {
                                    case "Activos" -> e.getEstado() == 1;
                                    case "Inactivos" -> e.getEstado() == 0;
                                    default -> true;
                                };
                            }
                            return coincideTexto && coincideEstado;
                        })
                        .collect(Collectors.toList());
            }

            @Override
            protected void succeeded() {
                tablaEmpleado.setItems(FXCollections.observableArrayList(getValue()));
                spinner.setVisible(false);
                botonBusquedaEmpleado.setDisable(false);
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                botonBusquedaEmpleado.setDisable(false);
            }
        };

        new Thread(tarea).start();
    }

    @FXML protected void irResguardo() { cambiarVista("/com/example/integradora/VistaResguardo.fxml"); }
    @FXML protected void irBienes() { cambiarVista("/com/example/integradora/VistaBienes.fxml"); }
    @FXML protected void irEmpleados() { cambiarVista("/com/example/integradora/VistaEmpleado.fxml"); }
    @FXML protected void irEspacio() { cambiarVista("/com/example/integradora/VistaEspacio.fxml"); }
    @FXML protected void irUnidad() { cambiarVista("/com/example/integradora/VistaUnidadAdm.fxml"); }
    @FXML protected void irEdificio() { cambiarVista("/com/example/integradora/VistaEdificio.fxml"); }
    @FXML protected void irUsuario() { cambiarVista("/com/example/integradora/VistaUsuario.fxml"); }
    @FXML protected void irPuesto() { cambiarVista("/com/example/integradora/VistaPuesto.fxml"); }
    private void cambiarVista(String rutaFXML) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(rutaFXML));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) usuario.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
