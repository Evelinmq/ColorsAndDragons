package com.example.integradora.controllers;

import com.example.integradora.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class BienvenidaController implements Initializable {

    @FXML
    private AnchorPane padreBienvenida;
    @FXML
    private Button bienes,resguardo, puesto, empleados, espacio, edificio, unidad, usuario;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    protected void irResguardo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) resguardo.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irBienes(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) bienes.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) empleados.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    @FXML
    protected void irEdificio(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) edificio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) usuario.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUnidad(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) unidad.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaPuesto.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) puesto.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //Sacar la stage desde un componente visual ya abieto
            Stage stage = (Stage) espacio.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
