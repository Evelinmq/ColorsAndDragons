package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Directora;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.modelo.dao.DirectoraDao;
import com.example.integradora.utils.OracleDatabaseConnectionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
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
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class DirectoraController implements Initializable {

    @FXML
    private TableView<Directora> tabla;
    @FXML
    private TableColumn<Directora, Integer> numeroTabla;
    @FXML
    private TableColumn<Directora, String> fechaTabla;
    @FXML
    private TableColumn<Directora, String> empleadoTabla;
    @FXML
    private TableColumn<Directora, String> espacioTabla;
    @FXML
    private Button descargar;

    @FXML
    private TextField textoBusqueda;
    @FXML
    private Button botonBusqueda;
    @FXML
    private ComboBox<String> filtro;

    private final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private ObservableList<Directora> masterData;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarFiltro();
        cargarDatos();

        // Filtrado sobre masterData
        FilteredList<Directora> filtered = new FilteredList<>(masterData, d -> true);

        textoBusqueda.textProperty().addListener((obs, old, val) -> {
            aplicarPredicado(filtered, val, filtro.getValue());
        });

        filtro.valueProperty().addListener((obs, old, val) -> {
            aplicarPredicado(filtered, textoBusqueda.getText(), val);
        });


        botonBusqueda.setOnAction(e -> {
            String f = filtro.getValue();
            String t = textoBusqueda.getText();
            var resultados = new DirectoraDao().search(f, t);

            // Mantener SortedList/FilteredList:
            masterData.setAll(resultados);

        });

        SortedList<Directora> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(sorted);
    }

    private void configurarTabla() {
        // ID
        numeroTabla.setCellValueFactory(c ->
                new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getIdResguardo()));
        // Fecha formateada
        fechaTabla.setCellValueFactory(c -> {
            String s = (c.getValue().getFecha() == null) ? "" : c.getValue().getFecha().format(DF);
            return new javafx.beans.property.SimpleStringProperty(s);
        });
        // Empleado
        empleadoTabla.setCellValueFactory(c -> {
            String s = c.getValue().getNombreEmpleadoCompleto();
            if (s.isBlank()) s = c.getValue().getRfcEmpleado();
            return new javafx.beans.property.SimpleStringProperty(s);
        });
        // Espacio
        espacioTabla.setCellValueFactory(c ->
                new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreEspacio()));
    }

    private void configurarFiltro() {
        filtro.setItems(FXCollections.observableArrayList(
                "Todos", "ID", "Fecha", "RFC", "Empleado", "Espacio", "Estado"
        ));
        filtro.setValue("Todos");
        filtro.setConverter(new StringConverter<>() {
            @Override
            public String toString(String s) {
                return s;
            }

            @Override
            public String fromString(String s) {
                return s;
            }
        });
    }

    private void cargarDatos() {
        masterData = FXCollections.observableArrayList(new DirectoraDao().readDirectora());
    }

    private void aplicarPredicado(FilteredList<Directora> filtered, String texto, String filtroActual) {
        String q = (texto == null) ? "" : texto.trim().toLowerCase();

        filtered.setPredicate(d -> {
            if (q.isEmpty()) return true;

            return switch (filtroActual) {
                case "ID" -> String.valueOf(d.getIdResguardo()).contains(q);
                case "Fecha" -> {
                    String f = (d.getFecha() == null) ? "" : d.getFecha().format(DF).toLowerCase();
                    // acepta dd/MM/yyyy parcial
                    yield f.contains(q);
                }
                case "RFC" -> d.getRfcEmpleado().toLowerCase().contains(q);
                case "Empleado" -> d.getNombreEmpleadoCompleto().toLowerCase().contains(q);
                case "Espacio" -> d.getNombreEspacio().toLowerCase().contains(q);
                case "Estado" ->
                        String.valueOf(d.getEstado()).contains(q) || d.getEstadoTexto().toLowerCase().contains(q);
                default ->  // Todos
                        d.getRfcEmpleado().toLowerCase().contains(q)
                                || d.getNombreEmpleadoCompleto().toLowerCase().contains(q)
                                || d.getNombreEspacio().toLowerCase().contains(q)
                                || String.valueOf(d.getIdResguardo()).contains(q)
                                || String.valueOf(d.getEstado()).contains(q)
                                || d.getEstadoTexto().toLowerCase().contains(q)
                                || ((d.getFecha() != null) && d.getFecha().format(DF).toLowerCase().contains(q));
            };
        });
    }

    @FXML
    private void descargarResguardoPdf() {
        Directora seleccionado = tabla.getSelectionModel().getSelectedItem();

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

            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_RESGUARDO", seleccionado.getIdResguardo());

            // Llenar el reporte
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

            // Exportar a PDF
            String nombreArchivo = "resguardo_" + seleccionado.getIdResguardo() + ".pdf";
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


