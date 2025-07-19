package com.example.integradora.controllers;

import com.example.integradora.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

    public class HelloController {

    @FXML
    private Label Inicio;

    @FXML
    protected void IrBienvenido() {
       try{
           FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("VistaBienvenida.fxml"));
           Scene scene = new Scene(fxmlLoader.load());
           Stage stage = (Stage) Inicio.getScene().getWindow();
           stage.setScene(scene);
           stage.show();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }
}


