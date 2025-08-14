package com.example.integradora.controllers;

import com.example.integradora.modelo.*;
import com.example.integradora.modelo.dao.*;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UpdateResguardoController implements Initializable {
    @FXML
    private DatePicker datePickerFecha;

    @FXML
    private ComboBox<Empleado> comboEmpleado;

    @FXML
    private ComboBox<Espacio> comboEspacio;

    @FXML
    private ComboBox<Bien> comboBien;

    @FXML
    private TableView<Bien> tablaBienes;
    @FXML
    private TableColumn<Bien, String> codigo, descripcion, marca, modelo, serie;
    @FXML
    private Button btnGuardar, cancelar, botonEliminar;

    private Resguardo resguardo;

    private final ResguardoDao resguardoDao = new ResguardoDao();
    private final ResguardoBienDao resguardoBienDao = new ResguardoBienDao();
    private final BienDao bienDao = new BienDao();

    private List<Bien> bienesOriginales;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCombos();
        configurarColumnas();
        // Cargar bienes disponibles solo una vez
        cargarComboBienesDisponibles();
    }

    public void setResguardo(Resguardo resguardo) {
        this.resguardo = resguardo;
        // La carga de datos debe hacerse después de que el resguardo ha sido establecido
        if (resguardo != null) {
            cargarDatosResguardo();
            cargarBienes(resguardo.getId());
        }
    }

    private void configurarColumnas() {
        codigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        marca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        serie.setCellValueFactory(new PropertyValueFactory<>("serie"));
    }

    private void cargarDatosResguardo() {
        datePickerFecha.setValue(resguardo.getFecha());
        // Ajuste en la selección de combo box
        comboEmpleado.getSelectionModel().select(resguardo.getEmpleado());
        comboEspacio.getSelectionModel().select(resguardo.getEspacio());
    }

    @FXML
    private void guardarCambios() {
        if (resguardo == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "No existe un resguardo para actualizar.");
            return;
        }

        LocalDate nuevaFecha = datePickerFecha.getValue();
        Empleado nuevoEmpleado = comboEmpleado.getValue();
        Espacio nuevoEspacio = comboEspacio.getValue();

        if (nuevaFecha == null || nuevoEmpleado == null || nuevoEspacio == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Faltan datos por llenar.");
            return;
        }

        int idResguardo = resguardo.getId();
        List<Bien> bienesActuales = new ArrayList<>(tablaBienes.getItems());

        // Actualizar el resguardo principal
        Date fechaSQL = Date.valueOf(nuevaFecha);
        boolean exitoUpdateResguardo = resguardoDao.updateResguardo(
                idResguardo,
                fechaSQL,
                nuevoEmpleado.getRfc(),
                nuevoEspacio.getId()
        );

        if (!exitoUpdateResguardo) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Ocurrió un problema al actualizar el resguardo.");
            return;
        }

        // 1. Identificar bienes a eliminar y a agregar
        List<Bien> bienesEliminados = bienesOriginales.stream()
                .filter(b -> !bienesActuales.contains(b))
                .collect(Collectors.toList());

        List<Bien> bienesAgregados = bienesActuales.stream()
                .filter(b -> !bienesOriginales.contains(b))
                .collect(Collectors.toList());

        // 2. Eliminar bienes
        for (Bien bien : bienesEliminados) {
            ResguardoBienDao.deleteResguardoBien(bien.getBien_codigo(), idResguardo);
        }

        // 3. Agregar nuevos bienes
        if (!bienesAgregados.isEmpty()) {
            List<ResguardoBien> nuevosResguardoBienes = new ArrayList<>();
            for (Bien bien : bienesAgregados) {
                ResguardoBien rb = new ResguardoBien();
                rb.setResguardo(resguardo);
                rb.setBien(bien);
                rb.setEspacio(nuevoEspacio);
                rb.setEdificio(nuevoEspacio.getEdificio());
                rb.setEmpleado(nuevoEmpleado);
                rb.setUnidad(nuevoEmpleado.getUnidadAdministrativa());
                rb.setPuesto(nuevoEmpleado.getPuesto());
                nuevosResguardoBienes.add(rb);
            }
            resguardoBienDao.insertarResguardoBien(nuevosResguardoBienes);
        }

        mostrarAlerta(Alert.AlertType.INFORMATION, "ÉXITO", "Se actualizó el resguardo correctamente.");
        cerrarVentana();
    }


    @FXML
    private void agregarBien() {
        Bien bienSeleccionado = comboBien.getValue();
        if (bienSeleccionado != null && !tablaBienes.getItems().contains(bienSeleccionado)) {
            tablaBienes.getItems().add(bienSeleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "ADVERTENCIA", "El bien ya existe en la tabla o no ha seleccionado uno.");
        }
    }

    @FXML
    private void eliminarBienSeleccionado() {
        Bien seleccionado = tablaBienes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            tablaBienes.getItems().remove(seleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "ADVERTENCIA", "Debes seleccionar un bien para eliminar.");
        }
    }

    private void cargarCombos() {
        List<Empleado> empleados = EmpleadoDao.readEmpleadosActivos();
        List<Espacio> espacios = EspacioDao.readEspaciosActivos();

        comboEmpleado.setItems(FXCollections.observableArrayList(empleados));
        comboEspacio.setItems(FXCollections.observableArrayList(espacios));

        comboEmpleado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Empleado object) {
                if (object == null) return "";
                return object.getNombre() + " " + object.getApellidoPaterno() + " " + object.getApellidoMaterno();
            }

            @Override
            public Empleado fromString(String string) {
                return comboEmpleado.getItems().stream()
                        .filter(e -> (e.getNombre() + " " + e.getApellidoPaterno() + " " + e.getApellidoMaterno()).equals(string))
                        .findFirst().orElse(null);
            }
        });

        comboEspacio.setConverter(new StringConverter<>() {
            @Override
            public String toString(Espacio object) {
                if (object == null) return "";
                return object.getNombre();
            }

            @Override
            public Espacio fromString(String string) {
                return comboEspacio.getItems().stream()
                        .filter(e -> e.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    private void cargarComboBienesDisponibles() {
        List<Bien> bienes = bienDao.readTodosBienes();
        comboBien.setItems(FXCollections.observableArrayList(bienes));
    }

    private void cargarBienes(int idResguardo) {
        List<Bien> bienes = ResguardoBienDao.obtenerBienesPorResguardo(idResguardo);
        bienesOriginales = new ArrayList<>(bienes);
        tablaBienes.setItems(FXCollections.observableArrayList(bienes));
    }

    @FXML
    private void cerrarVentana() {
        Stage ventana = (Stage) cancelar.getScene().getWindow();
        ventana.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}
