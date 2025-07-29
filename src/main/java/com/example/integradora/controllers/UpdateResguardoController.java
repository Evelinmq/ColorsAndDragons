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

import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
    private Button guardar, cancelar, botonEliminar;


    private Resguardo resguardo;

    private final ResguardoDao resguardoDao = new ResguardoDao();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarCombos();
        configurarColumnas();
        cargarComboBienesDisponibles();
        if (resguardo != null) {
            cargarDatosResguardo();
            cargarBienes(resguardo.getId());
        }
    }

    private List<Bien> bienesOriginales;

    private void configurarColumnas() {
        codigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        marca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        modelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        serie.setCellValueFactory(new PropertyValueFactory<>("serie"));
    }

    private void cargarDatosResguardo() {
        datePickerFecha.setValue(resguardo.getFecha());
        comboEmpleado.getSelectionModel().select(resguardo.getEmpleado());
        comboEspacio.getSelectionModel().select(resguardo.getEspacio());
    }

    @FXML
    private void guardarCambios() {
        if (resguardo == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "No existe un resguardo");
            return;
        }

        // Obtener nuevos valores del formulario
        LocalDate nuevaFecha = datePickerFecha.getValue();
        Empleado nuevoEmpleado = comboEmpleado.getValue();
        Espacio nuevoEspacio = comboEspacio.getValue();

        if (nuevaFecha == null || nuevoEmpleado == null || nuevoEspacio == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Faltan datos por llenar");
            return;
        }

        int idResguardo = resguardo.getId();
        List<Bien> bienesActuales = new ArrayList<>(tablaBienes.getItems());

        // Actualizar en base de datos
        Date fechaSQL = Date.valueOf(nuevaFecha);
        boolean exito = resguardoDao.updateResguardo(
                idResguardo,
                fechaSQL,
                nuevoEmpleado.getRfc(),
                nuevoEspacio.getId()
        );

        if (!exito) {
            mostrarAlerta(Alert.AlertType.ERROR, "ERROR", "Ocurrió un problema al actualizar el resguardo.");
            return;
        }

        // Actualizar objeto en memoria
        resguardo.setFecha(nuevaFecha);
        resguardo.setEmpleado(nuevoEmpleado);
        resguardo.setEspacio(nuevoEspacio);

        // Actualizar bienes
        for (Bien bien : bienesOriginales) {
            if (!bienesActuales.contains(bien)) {
                ResguardoBienDao.deleteResguardoBien(bien.getBien_codigo(), idResguardo);
            }
        }

        for (Bien bien : bienesActuales) {
            if (!bienesOriginales.contains(bien)) {
                ResguardoBienDao.insertarResguardoBien(idResguardo, bien.getBien_codigo());
            }
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
            mostrarAlerta(Alert.AlertType.WARNING, "ERROR", "El bien ya existe en la tabla o es nulo");
        }
    }

    @FXML
    private void eliminarBienSeleccionado() {
        Bien seleccionado = tablaBienes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            tablaBienes.getItems().remove(seleccionado);
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "ERROR", "Debes seleccionar un bien para eliminar");
        }
    }


    public void setResguardo(Resguardo resguardo) {
        this.resguardo = resguardo;
    }


    private void cargarCombos() {
        comboEmpleado.setItems(FXCollections.observableArrayList(EmpleadoDao.readEmpleados()));
        comboEspacio.setItems(FXCollections.observableArrayList(EspacioDao.readTodosEspacios()));
    }

    private void cargarComboBienesDisponibles() {
        List<Bien> bienes = BienDao.readTodosBienes();
        comboBien.setItems(FXCollections.observableArrayList(bienes));
    }

    private void cargarBienes(int idResguardo) {
        List<Bien> bienes = ResguardoBienDao.obtenerBienesPorResguardo(idResguardo);
        bienesOriginales = new ArrayList<>(bienes); // Guardar copia de los bienes para luego comparar con los nuevos
        tablaBienes.setItems(FXCollections.observableArrayList(bienes));
    }


    private void cerrarVentana() {
        Stage ventana = (Stage) tablaBienes.getScene().getWindow();
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
