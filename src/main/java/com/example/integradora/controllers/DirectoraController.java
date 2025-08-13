package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.modelo.dao.DirectoraDao;
import com.example.integradora.modelo.dao.ResguardoDao;
import com.example.integradora.utils.OracleDatabaseConnectionManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.*;
import java.util.List;


public class DirectoraController implements Initializable {

    @FXML
    private TableView<Resguardo> tabla;
    @FXML
    private TableColumn<Resguardo, Integer> numeroTabla;
    @FXML
    private TableColumn<Resguardo, String> fechaTabla;
    @FXML
    private TableColumn<Resguardo, String> empleadoTabla;
    @FXML
    private TableColumn<Resguardo, String> espacioTabla;

    @FXML
    private Button descarga;
    @FXML
    private TextField textoBusqueda;
    @FXML
    private Button botonBuscar;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private ComboBox<String> filtro;

    private ObservableList<Resguardo> masterData = FXCollections.observableArrayList(ResguardoDao.readTodosResguardos());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabla.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        configurarTabla();
        masterData = FXCollections.observableArrayList(ResguardoDao.readTodosResguardos());
        tabla.setItems(masterData);

        configurarFiltro();

        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            descarga.setDisable(!haySeleccion || (newSelection != null && newSelection.getEstado() != 1));
        });

        botonBuscar.setOnAction(this::buscar);
    }

    private void configurarFiltro() {
        filtro.getItems().addAll("Ver todos", "Activos", "Inactivos");
        filtro.setValue("Ver todos");

        filtro.valueProperty().addListener((obs, oldVal, newVal) -> {
            cargarResguardosPorEstado(newVal);
        });
    }

    private void configurarTabla() {
        numeroTabla.setCellValueFactory(new PropertyValueFactory<>("id"));
        fechaTabla.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFecha().toString()));
        empleadoTabla.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmpleado().getNombre()));
        espacioTabla.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEspacio().getNombre()));
    }

    private void cargarResguardosPorEstado(String opcion) {
        List<Resguardo> lista = new ArrayList<>();
        switch (opcion) {
            case "Activos":
                lista = DirectoraDao.readDirectoraPorEstado(1);
                break;
            case "Inactivos":
                lista = DirectoraDao.readDirectoraPorEstado(0);
                break;
            case "Ver todos":
                lista = ResguardoDao.readTodosResguardos();
                break;
        }
        tabla.setItems(FXCollections.observableList(lista));
        tabla.refresh();
    }

    @FXML
    private void buscar(ActionEvent event) {
        botonBuscar.setDisable(true);
        spinner.setVisible(true);

        String texto = textoBusqueda.getText().trim();

        Task<List<Resguardo>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Resguardo> call() throws Exception {
                return ResguardoDao.readResguardoEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBuscar.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Error: " + cargarBusqueda.getException());
            mostrarAlerta("Error de búsqueda", "Ocurrió un error al realizar la búsqueda: " + cargarBusqueda.getException().getMessage());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBuscar.setDisable(false);
            spinner.setVisible(false);
            List<Resguardo> lista = cargarBusqueda.getValue();
            tabla.setItems(FXCollections.observableList(lista));
            tabla.refresh();
        });

        Thread thread = new Thread(cargarBusqueda);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    protected void descarga(ActionEvent event) {
        Resguardo seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Error de informe", "Debes seleccionar un resguardo para poder descargar el informe.");
            return;
        }


        Task<Void> generarReporteTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                InputStream input = getClass().getResourceAsStream("/Resguardo.jasper");
                if (input == null) {
                    throw new IOException("No se pudo encontrar el archivo del informe");
                }
                JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

                Connection conexion = OracleDatabaseConnectionManager.getConnection();
                if (conexion == null || conexion.isClosed()) {
                    throw new Exception("No se pudo establecer la conexión a la base de datos.");
                }
                Image logo = ImageIO.read(new File("src/main/resources/com/example/integradora/jasper/UtezLogo.png"));
                //Image logo = ImageIO.read(getClass().getResourceAsStream("/UtezLogo.png"));
                Map<String, Object> parametros = new HashMap<>();

                parametros.put("NOMBRE_ESPACIO", seleccionado.getEspacio().getNombre());
                parametros.put("FECHA_RESGUARDO", seleccionado.getFecha());
                parametros.put("logo", logo);

                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

                JasperViewer.viewReport(jasperPrint, false);

                return null;
            }
        };

        generarReporteTask.setOnSucceeded(e -> {
            mostrarAlerta("Informe generado", "El informe del resguardo se ha generado exitosamente.");
        });

        generarReporteTask.setOnFailed(e -> {
            Throwable exception = generarReporteTask.getException();
            System.err.println("Error al generar el informe: " + exception.getMessage());
            exception.printStackTrace();
            mostrarAlerta("Error al generar informe", "Ocurrió un error al generar el informe: " + exception.getMessage());
        });

        Thread thread = new Thread(generarReporteTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    protected void descargaTodos(ActionEvent event) {
        ObservableList<Resguardo> seleccionados = tabla.getSelectionModel().getSelectedItems();

        if (seleccionados.isEmpty()) {
            mostrarAlerta("Error de informe", "Debes seleccionar uno o más resguardos para descargar los informes.");
            return;
        }

        Task<Void> generarReportesTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                for (Resguardo seleccionado : seleccionados) {
                    if (seleccionado.getEstado() != 1) {
                        System.out.println("Saltando resguardo con ID " + seleccionado.getId() + " porque su estado no es activo.");
                        continue;
                    }

                    InputStream input = getClass().getResourceAsStream("/RESGUARDO.jasper");
                    if (input == null) {
                        throw new IOException("No se pudo encontrar el archivo del informe");
                    }
                    JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

                    Connection conexion = OracleDatabaseConnectionManager.getConnection();
                    if (conexion == null || conexion.isClosed()) {
                        throw new Exception("No se pudo establecer la conexión a la base de datos.");
                    }

                    Image logo = ImageIO.read(new File("src/main/resources/com/example/integradora/jasper/UtezLogo.png"));
                    //Image logo = ImageIO.read(getClass().getResourceAsStream("/UtezLogo.png"));
                    Map<String, Object> parametros = new HashMap<>();
                    parametros.put("NOMBRE_ESPACIO", seleccionado.getEspacio().getNombre());
                    parametros.put("FECHA_RESGUARDO", seleccionado.getFecha());
                    parametros.put("logo", logo);

                    JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

                    JasperViewer.viewReport(jasperPrint, false);

                }

                return null;
            }
        };

        generarReportesTask.setOnSucceeded(e -> {
            mostrarAlerta("Informes generados", "Se han generado los informes de los resguardos seleccionados.");
        });

        generarReportesTask.setOnFailed(e -> {
            Throwable exception = generarReportesTask.getException();
            System.err.println("Error al generar informes: " + exception.getMessage());
            exception.printStackTrace();
            mostrarAlerta("Error al generar informes", "Ocurrió un error al generar los informes: " + exception.getMessage());
        });

        Thread thread = new Thread(generarReportesTask);
        thread.setDaemon(true);
        thread.start();
    }
    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Cierre de Sesión");
        alert.setHeaderText("Estás a punto de cerrar la sesión.");
        alert.setContentText("¿Estás seguro de que quieres cerrar la sesión?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/IniciarSesion.fxml"));
                Parent newRoot = fxmlLoader.load();
                Stage stage = (Stage) descarga.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setRoot(newRoot);
                stage.setTitle("Iniciar Sesión");
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de carga");
                errorAlert.setHeaderText("Error al cargar la vista de inicio de sesión.");
                errorAlert.setContentText("No se pudo cargar la vista de inicio de sesión");
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }
    }
}



