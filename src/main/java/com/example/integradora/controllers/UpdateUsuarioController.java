package com.example.integradora.controllers;

import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateUsuarioController implements Initializable {


    @FXML
    private TextField correo;
    @FXML
    private TextField contrasena;
    @FXML
    private TextField rfcEmpleado;
    @FXML
    private TextField idUnidad;
    @FXML
    private TextField idPuesto;
    @FXML
    private TextField estado;


    private Usuario usuario;
    private String correoViejo;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.correoViejo = usuario.getCorreo();


        correo.setText(usuario.getCorreo());
        contrasena.setText(usuario.getContrasena());
        rfcEmpleado.setText(usuario.getRfcEmpleado());
        idUnidad.setText(String.valueOf(usuario.getIdUnidad()));
        idPuesto.setText(String.valueOf(usuario.getIdPuesto()));
        estado.setText(String.valueOf(usuario.getEstado()));
    }


    @FXML
    public void updateUsuario(ActionEvent event) {


        String nuevoCorreo = correo.getText();
        String nuevaContrasena = contrasena.getText();
        String nuevoRfc = rfcEmpleado.getText();
        int nuevaUnidad = Integer.parseInt(idUnidad.getText());
        int nuevoPuesto = Integer.parseInt(idPuesto.getText());
        int nuevoEstado = Integer.parseInt(estado.getText());


        usuario.setCorreo(nuevoCorreo);
        usuario.setContrasena(nuevaContrasena);
        usuario.setRfcEmpleado(nuevoRfc);
        usuario.setIdUnidad(nuevaUnidad);
        usuario.setIdPuesto(nuevoPuesto);
        usuario.setEstado(nuevoEstado);


        UsuarioDao dao = new UsuarioDao();


        if (dao.updateUsuario(correoViejo, usuario)) {
            System.out.println("Usuario actualizado");
        }


        Stage ventana = (Stage) correo.getScene().getWindow();
        ventana.close();
    }
}


