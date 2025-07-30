package com.example.integradora.controllers;

import com.example.integradora.modelo.Directora;
import com.example.integradora.modelo.dao.DirectoraDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DirectoraController implements Initializable {

    @FXML private TableView<Directora> tabla;
    @FXML private TableColumn<Directora, Integer> numeroTabla;
    @FXML private TableColumn<Directora, String> fechaTabla;
    @FXML private TableColumn<Directora, String> empleadoTabla;
    @FXML private TableColumn<Directora, String> espacioTabla;

    @FXML private TextField textoBusqueda;
    @FXML private Button botonBusqueda;
    @FXML private ComboBox<String> filtro;

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
            @Override public String toString(String s) { return s; }
            @Override public String fromString(String s) { return s; }
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
                case "Estado" -> String.valueOf(d.getEstado()).contains(q) || d.getEstadoTexto().toLowerCase().contains(q);
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
}


