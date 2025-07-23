package com.example.integradora.controllers;

import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import java.util.Optional;

public class UsuarioController {
    @FXML
    private TextField correoUsuario;
    @FXML
    private TextField contraseniaUsuario;
    @FXML
    private TextField rolUsuario;
    @FXML
    private Button guardarUsuario;
    @FXML
    private Button cancelarUsuario;

    private UsuarioDao usuarioDAO = new UsuarioDao();

    @FXML
    private void initialize() {
        guardarUsuario.setOnAction(event -> crearUsuario());
    }

    private void crearUsuario() {
        String correo = correoUsuario.getText().trim();
        String contrasena = contraseniaUsuario.getText().trim();
        String rol = rolUsuario.getText().trim();

        if (correo.isEmpty() || contrasena.isEmpty() || rol.isEmpty()) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasena);
        nuevo.setRol(rol);

        boolean creado = usuarioDAO.createUsuario(nuevo);

        if (creado) {
            System.out.println("Usuario creado exitosamente");
            correoUsuario.clear();
            contraseniaUsuario.clear();
            rolUsuario.clear();
        } else {
            mostrarAlerta("No se pudo crear el usuario.");
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
