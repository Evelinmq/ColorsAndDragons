package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.controllers.PuestoController;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.util.List;


public class RegistrarUsuarioController {

    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasenia;
    @FXML private TextField txtRol;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage stage;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void guardarUsuario(ActionEvent event) {
        System.out.println("Guardando Usuario");

        String correo = txtCorreo.getText().trim();
        String contrasenia = txtContrasenia.getText().trim();
        String rolTexto = txtRol.getText().trim();

        if (correo.isEmpty() || contrasenia.isEmpty() || rolTexto.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos Vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Todos los campos deben estar llenos.");
            alerta.showAndWait();
            return;
        }

        int rol;
        try {
            rol = Integer.parseInt(rolTexto);
        } catch (NumberFormatException e) {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error de formato");
            alerta.setHeaderText(null);
            alerta.setContentText("El rol debe ser un número.");
            alerta.showAndWait();
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasenia);
        nuevo.setRol(rol);
        nuevo.setEstado(1); // activo por defecto

        UsuarioDao dao = new UsuarioDao();
        boolean exito = dao.createUsuario(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Se ha registrado un nuevo usuario.");
            alert.showAndWait();
            cerrarVentana(event);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("No se pudo registrar el usuario.");
            alert.showAndWait();
        }
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        if (stage != null) {
            stage.close();
        } else {
            Stage currentStage = (Stage) txtCorreo.getScene().getWindow();
            currentStage.close();
        }
    }
}
