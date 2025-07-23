package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.dao.EmpleadoDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;

import java.util.Optional;

public class EmpleadoController {
    @FXML
    private TextField nombreEmpleado;
    @FXML
    private TextField apellidoPaterno;
    @FXML
    private TextField apellidoMaterno;
    @FXML
    private TextField rfc;
    @FXML
    private Button guardarEmpleado;
    @FXML
    private Button cancelarEmpleado;

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