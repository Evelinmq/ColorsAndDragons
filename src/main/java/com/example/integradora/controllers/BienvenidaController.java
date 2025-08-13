package com.example.integradora.controllers;

import com.example.integradora.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class BienvenidaController implements Initializable {

    @FXML
    private AnchorPane padreBienvenida;
    @FXML
    private Button bienes, resguardo, puesto, empleados, espacio, edificio, unidad, usuario;

    private List<Button> menuButtons;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }



    @FXML
    protected void irResguardo() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) resguardo.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irBienes() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) bienes.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) empleados.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) edificio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) usuario.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) unidad.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) puesto.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio() {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) espacio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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
                Parent newRoot = fxmlLoader.load();
                Stage stage = (Stage) usuario.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setRoot(newRoot);



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