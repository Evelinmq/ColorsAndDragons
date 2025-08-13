package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Resguardo;
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
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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

public class ResguardoController implements Initializable {

    @FXML
    private Button puesto, bienes, empleados, espacio, unidad, edificio, usuario, resguardo;
    @FXML
    private Button agregar, eliminar, editar, recuperar, descarga, botonBuscar;
    @FXML
    private TableView<Resguardo> tablaResguardo;
    @FXML
    private TableColumn<Resguardo, String> fecha, empleado;
    @FXML
    private TableColumn<Resguardo, Integer> numero;
    @FXML
    private ProgressIndicator spinner;
    @FXML
    private ComboBox filtro;
    @FXML
    private TextField buscador;
    private List<Button> menuButtons;

    private final ResguardoDao dao = new ResguardoDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuButtons = new ArrayList<>();
        menuButtons.add(resguardo);
        menuButtons.add(unidad);
        menuButtons.add(empleados);
        menuButtons.add(espacio);
        menuButtons.add(puesto);
        menuButtons.add(edificio);
        menuButtons.add(usuario);
        menuButtons.add(bienes);

        resguardo.getStyleClass().add("menu-button-selected");

        configurarTabla();
        configurarFiltro();
        cargarResguardosPorEstado();

        tablaResguardo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editar.setDisable(!haySeleccion || newSelection.getEstado() == 0);
            eliminar.setDisable(!haySeleccion || newSelection.getEstado() == 0);
            recuperar.setDisable(!haySeleccion || newSelection.getEstado() != 0);
            descarga.setDisable(!haySeleccion || (newSelection != null && newSelection.getEstado() != 1));
        });

        editar.setOnAction(e -> {
            Resguardo seleccionado = tablaResguardo.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                abrirVentanaEdicion(seleccionado);
            } else {
                mostrarAlerta("Debes seleccionar un resguardo para editar");
            }
        });

        eliminar.setOnAction(e -> {
            Resguardo seleccionado = tablaResguardo.getSelectionModel().getSelectedItem();
            if (seleccionado != null && confirmarEliminar()) {
                if (dao.deleteResguardo(seleccionado.getId())) {
                    mostrarAlerta("Resguardo eliminado.");
                    cargarResguardosPorEstado();
                }
            }
        });

        recuperar.setOnAction(e -> {
            Resguardo seleccionado = tablaResguardo.getSelectionModel().getSelectedItem();
            if (seleccionado != null && confirmarRecuperar()) {
                if (dao.restaurarResguardo(seleccionado.getId())) {
                    mostrarAlerta("Resguardo restaurado");
                    cargarResguardosPorEstado();
                }
            }
        });

        agregar.setOnAction(e -> abrirVentanaRegistro());

        botonBuscar.setOnAction(this::buscar);
    }

    private void configurarTabla() {
        numero.setCellValueFactory(new PropertyValueFactory<>("id"));
        fecha.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFecha().toString()));
        empleado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmpleado().getNombre()));
    }

    private void configurarFiltro() {
        filtro.getItems().addAll("Activos", "Inactivos", "Ver Todos");
        filtro.setValue("Ver Todos");
        filtro.setOnAction(e -> cargarResguardosPorEstado());
    }


    @FXML
    private void cargarResguardosPorEstado() {
        String opcion = filtro.getValue().toString();
        List<Resguardo> lista = new ArrayList<>();

        switch (opcion) {
            case "Activos":
                lista = dao.readResguardoPorEstado(1);
                break;
            case "Inactivos":
                lista = dao.readResguardoPorEstado(0);
                break;
            case "Ver Todos":
                lista = dao.readTodosResguardos();
                break;
        }

        tablaResguardo.setItems(FXCollections.observableList(lista));
        tablaResguardo.refresh();
        recuperar.setDisable(true);
    }

    private void resetAllButtons() {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("menu-button-selected");
        }
    }


    private void abrirVentanaEdicion(Resguardo resguardo) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditarResguardo.fxml"));
            Parent root = loader.load();
            UpdateResguardoController controller = loader.getController();
            controller.setResguardo(resguardo);

            Scene nuevaEscena = new Scene(root);
            nuevaEscena.getStylesheets().add(Main.class.getResource("/com/example/integradora/styles/styles.css").toExternalForm());

            Scene escenaPrincipal = editar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(nuevaEscena);
            stage.setTitle("Editar Resguardo");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                cargarResguardosPorEstado();
            });

            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("NuevoResguardo.fxml"));
            Parent root = loader.load();

            Scene nuevaEscena = new Scene(root);
            nuevaEscena.getStylesheets().add(Main.class.getResource("/com/example/integradora/styles/styles.css").toExternalForm());

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(nuevaEscena);
            stage.setTitle("Registrar Resguardo");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(escenaPrincipal.getWindow());

            stage.setOnHidden(e -> {
                fondo.setEffect(null);
                cargarResguardosPorEstado();
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
        alert.setContentText("¿Estás seguro de eliminar este resguardo?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private boolean confirmarRecuperar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar restauración");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas restaurar este resguardo?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }


    @FXML
    private void buscar(ActionEvent event) {
        botonBuscar.setDisable(true);
        spinner.setVisible(true);
        String texto = buscador.getText().trim();

        Task<List<Resguardo>> cargarBusqueda = new Task<>() {
            @Override
            protected List<Resguardo> call() {
                return dao.readResguardoEspecifico(texto);
            }
        };

        cargarBusqueda.setOnFailed(workerStateEvent -> {
            botonBuscar.setDisable(false);
            spinner.setVisible(false);
            System.err.println("Error: " + cargarBusqueda.getException());
        });

        cargarBusqueda.setOnSucceeded(workerStateEvent -> {
            botonBuscar.setDisable(false);
            spinner.setVisible(false);
            List<Resguardo> lista = cargarBusqueda.getValue();
            ObservableList<Resguardo> listaObservable = FXCollections.observableList(lista);
            tablaResguardo.setItems(listaObservable);
            tablaResguardo.refresh();
        });

        Thread thread = new Thread(cargarBusqueda);
        thread.setDaemon(true);
        thread.start();
    }


    //Botones cambiar a vistas
    @FXML
    protected void irBienes() {
        resetAllButtons();
        bienes.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) bienes.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados() {
        resetAllButtons();
        empleados.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) empleados.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio() {
        resetAllButtons();
        espacio.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) espacio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad() {
        resetAllButtons();
        unidad.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) unidad.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto() {
        resetAllButtons();
        puesto.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) puesto.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio() {
        resetAllButtons();
        edificio.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) edificio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario() {
        resetAllButtons();
        usuario.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) usuario.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Stage stage = (Stage) usuario.getScene().getWindow();
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

    @FXML
    protected void descarga(ActionEvent event) {
        Resguardo seleccionado = tablaResguardo.getSelectionModel().getSelectedItem();
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
}