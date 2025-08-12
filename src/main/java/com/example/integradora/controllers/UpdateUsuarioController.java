package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateUsuarioController implements Initializable {

    @FXML
    private TextField correo;
    @FXML
    private PasswordField contrasena;
    @FXML
    private ComboBox<String> cbRol;
    @FXML
    private ComboBox<Empleado> cbEmpleado;


    private Usuario usuario;
    private String correoViejo;
    private Stage stage;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        List<String> roles = new ArrayList<>();
        roles.add("Administrador");
        roles.add("Visualizador");
        cbRol.setItems(FXCollections.observableArrayList(roles));


    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.correoViejo = usuario.getCorreo();

        correo.setText(usuario.getCorreo());
        contrasena.setText(usuario.getContrasena());

        List<Empleado> empleadosActivos = EmpleadoDao.readEmpleadosActivos();
        cbEmpleado.setItems(FXCollections.observableArrayList(empleadosActivos));

        cbEmpleado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Empleado empleado) {
                if (empleado == null) return "";
                return empleado.getNombre() + " " + empleado.getApellidoPaterno() + " " + empleado.getApellidoMaterno();
            }
            @Override
            public Empleado fromString(String string) {
                return cbEmpleado.getItems().stream()
                        .filter(e -> (e.getNombre() + " " + e.getApellidoPaterno() + " " + e.getApellidoMaterno()).equals(string))
                        .findFirst().orElse(null);
            }
        });

        if (usuario.getRfcEmpleado() != null) {
            cbEmpleado.getItems().stream()
                    .filter(e -> e.getRfc().equals(usuario.getRfcEmpleado()))
                    .findFirst()
                    .ifPresent(empleadoEncontrado -> cbEmpleado.getSelectionModel().select(empleadoEncontrado));
        }


        if (usuario.getRol() != null) {
            cbRol.getSelectionModel().select(usuario.getRol());
        }
    }

    @FXML
    public void updateUsuario(ActionEvent event) {
        String nuevoCorreo = correo.getText();
        String nuevaContrasena = contrasena.getText();

        Empleado empleadoSeleccionado = cbEmpleado.getSelectionModel().getSelectedItem();
        String rolSeleccionado = cbRol.getSelectionModel().getSelectedItem();



        if (nuevoCorreo.isEmpty() || nuevaContrasena.isEmpty() || empleadoSeleccionado == null || rolSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor, llena todos los campos y selecciona las opciones.");
            return;
        }


        usuario.setCorreo(nuevoCorreo);
        usuario.setContrasena(nuevaContrasena);
        usuario.setRfcEmpleado(empleadoSeleccionado.getRfc());
        usuario.setRol(rolSeleccionado);


        UsuarioDao dao = new UsuarioDao();

        if (dao.updateUsuario(correoViejo, usuario)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "El usuario ha sido actualizado correctamente.");
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un problema al actualizar el usuario.");
        }

        cerrarVentana(event);
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}


