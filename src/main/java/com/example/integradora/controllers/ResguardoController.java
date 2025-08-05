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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.*;

public class ResguardoController implements Initializable {

    @FXML
    private Button puesto, bienes, empleados, espacio, unidad, edificio, usuario;
    @FXML
    private Button agregar, eliminar, editar, recuperar, descargar, botonBuscar;
    @FXML
    private TableView<Resguardo> tablaResguardo;
    @FXML
    private TableColumn<Resguardo, String> fecha, empleado;
    @FXML
    private TableColumn<Resguardo, Integer> numero;
    @FXML
    private Spinner spinner;
    @FXML
    private ComboBox filtro;
    @FXML
    private TextField buscador;

    private final ResguardoDao dao = new ResguardoDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarTabla();
        configurarFiltro();
        cargarResguardosPorEstado();

        tablaResguardo.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean haySeleccion = newSelection != null;
            editar.setDisable(!haySeleccion || newSelection.getEstado() == 0);
            eliminar.setDisable(!haySeleccion || newSelection.getEstado() == 0);
            recuperar.setDisable(!haySeleccion || newSelection.getEstado() != 0);
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
        filtro.getItems().addAll("Activos", "Inactivos", "VerTodo");
        filtro.setValue("Activos");
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
            case "VerTodo":
                lista = dao.readTodosResguardos();
                break;
        }

        tablaResguardo.setItems(FXCollections.observableList(lista));
        tablaResguardo.refresh();
        recuperar.setDisable(true);
    }


    private void abrirVentanaEdicion(Resguardo resguardo) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("EditarResguardo.fxml"));
            Parent root = loader.load();
            UpdateResguardoController controller = loader.getController();
            controller.setResguardo(resguardo);

            Scene escenaPrincipal = editar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
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

            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
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

    @FXML
    private void descargarResguardoPdf() {
        Resguardo seleccionado = tablaResguardo.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText(null);
            alerta.setContentText("Debes seleccionar un resguardo para generar el PDF");
            alerta.showAndWait();
            return;
        }

        try {
            // Cargar el archivo .jasper
            InputStream input = getClass().getResourceAsStream("/Oracle-Test.jasper");
            JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

            Connection conexion = OracleDatabaseConnectionManager.getConnection();

            // Parámetros a pasar al .jasper
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_RESGUARDO", seleccionado.getId()); // ID del resguardo seleccionado

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

            // Mostrar vista previa
            JasperViewer.viewReport(jasperPrint, false);

            // Exportar a PDF
            String nombreArchivo = "resguardo_" + seleccionado.getId() + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, nombreArchivo);

            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setHeaderText(null);
            alerta.setContentText("PDF generado correctamente: " + nombreArchivo);
            alerta.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setHeaderText("Error al generar PDF");
            error.setContentText(e.getMessage());
            error.showAndWait();
        }
    }


    //Botones cambiar a vistas
    @FXML
    protected void irBienes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) bienes.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) empleados.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) espacio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) unidad.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) puesto.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) edificio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) usuario.getScene().getWindow();
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
                Scene scene = new Scene(fxmlLoader.load());

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();


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
