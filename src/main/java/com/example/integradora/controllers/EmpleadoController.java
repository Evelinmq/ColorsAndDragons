package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.EmpleadoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmpleadoController {

    @FXML
    private TableView<Usuario> tablaEmpleado;
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
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario;
    @FXML
    private Button botonBusquedaEmpleado, eliminarEmpleado, actualizarEmpleado, agregar, recuperar;

    @FXML
    private TextField textoBusquedaEmpleado;

    @FXML
    private ProgressIndicator spinner;

    @FXML
    private ComboBox<String> filtroEstado;

    @FXML
    private TextField nombreEmpleado, apellidoPaterno, apellidoMaterno, rfc;

    @FXML
    private Button guardarEmpleado;

    private List<Usuario> Empleados = new ArrayList<>();

    private EmpleadoDao empleadoDAO = new EmpleadoDao();

    @FXML
    private void initialize() {
        guardarEmpleado.setOnAction(event -> crearEmpleado());
    }

    private void crearEmpleado() {
        String nombre = nombreEmpleado.getText().trim();
        String paterno = apellidoPaterno.getText().trim();
        String materno = apellidoMaterno.getText().trim();
        String claveRfc = rfc.getText().trim();

        if (nombre.isEmpty() || paterno.isEmpty() || materno.isEmpty() || claveRfc.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        Empleado nuevo = new Empleado();
        nuevo.setNombre(nombre);
        nuevo.setApellidoPaterno(paterno);
        nuevo.setApellidoMaterno(materno);
        nuevo.setRfc(claveRfc);

        boolean creado = empleadoDAO.createEmpleado(nuevo);

        if (creado) {
            System.out.println("Empleado creado exitosamente");
            nombreEmpleado.clear();
            apellidoPaterno.clear();
            apellidoMaterno.clear();
            rfc.clear();
        } else {
            mostrarAlerta("No se pudo crear el empleado.");
        }
    }

    public void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ERROR");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
    }
}
