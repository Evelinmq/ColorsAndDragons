package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.dao.EmpleadoDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegistrarEmpleadoController {

    @FXML public TextField txtNombre;
    @FXML public TextField txtApellidoPaterno;
    @FXML public TextField txtApellidoMaterno;
    @FXML public TextField txtRfc;
    @FXML public Button btnCancelar;
    @FXML public Button btnGuardar;

    private Stage stage;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Runnable onEmpleadoCreado;

    public void setOnEmpleadoCreado(Runnable onEmpleadoCreado) {
        this.onEmpleadoCreado = onEmpleadoCreado;
    }

    @FXML
    private void guardarEmpleado(ActionEvent event) {
        System.out.println("Guardando Empleado");

        String nombre = txtNombre.getText().trim();
        String apellidoP = txtApellidoPaterno.getText().trim();
        String apellidoM = txtApellidoMaterno.getText().trim();
        String rfc = txtRfc.getText().trim();

        if (nombre.isEmpty() || apellidoP.isEmpty() || apellidoM.isEmpty() || rfc.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos Vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Todos los campos deben estar llenos.");
            alerta.showAndWait();
            return;
        }

        Empleado nuevo = new Empleado();
        nuevo.setNombre(nombre);
        nuevo.setApellidoPaterno(apellidoP);
        nuevo.setApellidoMaterno(apellidoM);
        nuevo.setRfc(rfc);
        nuevo.setEstado(1); // Activo por defecto

        EmpleadoDao dao = new EmpleadoDao();
        boolean exito = dao.createEmpleado(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Se ha registrado un nuevo empleado.");
            alert.showAndWait();

            if (onEmpleadoCreado != null) {
                onEmpleadoCreado.run();
            }

            cerrarVentana(event); // cerrar si fue exitoso
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo registrar el empleado.");
            alert.showAndWait();
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        if (stage != null) {
            stage.close();
        } else {
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
    }
}
