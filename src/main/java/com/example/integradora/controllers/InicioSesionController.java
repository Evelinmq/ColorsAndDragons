package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import java.io.IOException;

public class InicioSesionController {
    @FXML
    private TextField correos;

    @FXML
    private javafx.scene.control.TextField contrasena;

    private UsuarioDao usuarioDao = new UsuarioDao();

    @FXML
    protected void inicioSesion(ActionEvent event) {
        String correo = correos.getText();
        String contrasenia = contrasena.getText();

        Usuario usuario = usuarioDao.Login(correo);

        if (usuario != null && BCrypt.checkpw(contrasenia, usuario.getContrasena())) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inicio de Sesión");
            alert.setHeaderText(null);
            alert.setContentText("¡Inicio de Sesión exitoso!");
            alert.showAndWait();
            System.out.println("¡Inicio de sesión exitoso!");
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            DirigirVista(usuario.getRol(), currentStage);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText(null);
            alert.setContentText("Error al iniciar Sesión");
            alert.showAndWait();
            System.out.println("Credenciales inválidas");
        }
    }

    private void DirigirVista(String rol, Stage currentStage) {
        try {
            String fxmlFile;
            String title;
            switch (rol) {
                case "Administrador":
                    fxmlFile = "/com/example/integradora/VistaBienvenida.fxml";
                    title = "Panel de Administrador";
                    break;
                case "Visualizador":
                    fxmlFile = "/com/example/integradora/VistaInformesDirectora.fxml";
                    title = "Panel del Visualizador";
                    break;
                default:
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText(null);
                    alert.setContentText("Error en el Rol, se mandara a la vista de Inicio");
                    alert.showAndWait();
                    fxmlFile = "/com/example/integradora/IniciarSesion.fxml";
                    title = "Panel de Usuario";
                    break;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load());
            currentStage.setTitle(title);
            currentStage.setScene(scene);
            currentStage.show();

        } catch (IOException e) {
            System.out.println("Error al cargar la vista");
            e.printStackTrace();
    }
}



    }


