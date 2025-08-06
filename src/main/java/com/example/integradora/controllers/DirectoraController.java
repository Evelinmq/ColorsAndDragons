package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Directora;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    private TableColumn<Directora, Void> espacioAccion;

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
            masterData.setAll(resultados);
        });

        SortedList<Directora> sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(tabla.comparatorProperty());
        tabla.setItems(sorted);
    }

    private void configurarTabla() {
        numeroTabla.setCellValueFactory(c -> new javafx.beans.property.SimpleObjectProperty<>(c.getValue().getIdResguardo()));

        fechaTabla.setCellValueFactory(c -> {
            String s = (c.getValue().getFecha() == null) ? "" : c.getValue().getFecha().format(DF);
            return new javafx.beans.property.SimpleStringProperty(s);
        });

        empleadoTabla.setCellValueFactory(c -> {
            String s = c.getValue().getNombreEmpleadoCompleto();
            if (s.isBlank()) s = c.getValue().getRfcEmpleado();
            return new javafx.beans.property.SimpleStringProperty(s);
        });

        espacioTabla.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombreEspacio()));

        agregarBotonDescargar();
    }

    private void agregarBotonDescargar() {
        Callback<TableColumn<Directora, Void>, TableCell<Directora, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Directora, Void> call(final TableColumn<Directora, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button("Descargar PDF");

                    {
                        btn.setStyle("-fx-background-color: #B2BCDB; -fx-text-fill: #0033cc; -fx-background-radius: 15;");
                        btn.setOnAction((e) -> {
                            Directora data = getTableView().getItems().get(getIndex());
                            descargarPdfDesdeFila(data);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        };

        espacioAccion.setCellFactory(cellFactory);
    }

    private void configurarFiltro() {
        filtro.setItems(FXCollections.observableArrayList("Todos", "ID", "Fecha", "RFC", "Empleado", "Espacio", "Estado"));
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
                    yield f.contains(q);
                }
                case "RFC" -> d.getRfcEmpleado().toLowerCase().contains(q);
                case "Empleado" -> d.getNombreEmpleadoCompleto().toLowerCase().contains(q);
                case "Espacio" -> d.getNombreEspacio().toLowerCase().contains(q);
                case "Estado" ->
                        String.valueOf(d.getEstado()).contains(q) || d.getEstadoTexto().toLowerCase().contains(q);
                default ->
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

    private void descargarPdfDesdeFila(Directora seleccionado) {
        if (seleccionado == null) return;

        try {
            InputStream input = getClass().getResourceAsStream("/com/example/integradora/jasper/Resguardo.jasper");
            JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

            Connection conexion = OracleDatabaseConnectionManager.getConnection();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_RESGUARDO", seleccionado.getIdResguardo());

            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);
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
    private void descargarResguardoPdf() {
        ObservableList<Directora> seleccionados = tabla.getSelectionModel().getSelectedItems();

        if (seleccionados == null || seleccionados.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setHeaderText(null);
            alerta.setContentText("Debes seleccionar al menos un resguardo para generar el PDF");
            alerta.showAndWait();
            return;
        }
        descargarMultiplesPdf(seleccionados);
    }

    private void descargarMultiplesPdf(ObservableList<Directora> seleccionados) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Seleccionar carpeta para guardar PDFs");
        File carpetaDestino = dirChooser.showDialog(null);

        if (carpetaDestino == null) {
            return;
        }
        for (Directora resguardo : seleccionados) {
            descargarPdfEnCarpeta(resguardo, carpetaDestino.getAbsolutePath());
        }
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setHeaderText(null);
        alerta.setContentText("Se han generado todos los PDFs correctamente en la carpeta seleccionada.");
        alerta.showAndWait();
    }

    private void descargarPdfEnCarpeta(Directora seleccionado, String rutaDestino) {

        try {
            // Carga la plantilla del reporte
            InputStream input = getClass().getResourceAsStream("/com/example/integradora/jasper/Resguardo.jasper");
            JasperReport reporte = (JasperReport) JRLoader.loadObject(input);

            Connection conexion = OracleDatabaseConnectionManager.getConnection();
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("ID_RESGUARDO", seleccionado.getIdResguardo());

            // Llena el reporte con los datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, parametros, conexion);

            // Genera el nombre del archivo y la ruta completa
            String nombreArchivo = "resguardo_" + seleccionado.getIdResguardo() + ".pdf";
            String rutaCompleta = rutaDestino + File.separator + nombreArchivo;

            // Exporta el reporte a la ruta seleccionada por el usuario
            JasperExportManager.exportReportToPdfFile(jasperPrint, rutaCompleta);

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



