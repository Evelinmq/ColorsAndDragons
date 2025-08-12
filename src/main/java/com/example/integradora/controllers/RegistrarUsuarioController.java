package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.Usuario;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import com.example.integradora.modelo.dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class RegistrarUsuarioController {

    @FXML private TextField txfCorreo;
    @FXML private PasswordField txfContrasena;
    @FXML private ComboBox<String> cbRol;
    @FXML private ComboBox<Empleado> cbEmpleado;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Stage stage;

    public void setDialogStage(Stage stage) {
        this.stage = stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private Runnable onUsuarioCreado;

    public void initialize() {
        var roles = FXCollections.observableArrayList("Administrador", "Visualizador");
        cbRol.setItems(roles);

        List<Empleado> empleados = EmpleadoDao.readEmpleadosActivos();
        cbEmpleado.setItems(FXCollections.observableArrayList(empleados));

        cbEmpleado.setConverter(new StringConverter<Empleado>() {
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
    }




    public void setOnUsuarioCreado(Runnable onUsuarioCreado) {
        this.onUsuarioCreado = onUsuarioCreado;
    }

    @FXML
    private void guardarUsuario(ActionEvent event) {
        System.out.println("Guardando Usuario");

        String correo = txfCorreo.getText().trim();
        String contrasenia = txfContrasena.getText().trim();
        String rolSeleccionado = cbRol.getValue();


        System.out.println("Valor del rol seleccionado: " + rolSeleccionado);

        if (correo.isEmpty() || contrasenia.isEmpty() || rolSeleccionado == null || rolSeleccionado.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Campos Vacíos");
            alerta.setHeaderText(null);
            alerta.setContentText("Todos los campos deben estar llenos.");
            alerta.showAndWait();
            return;
        }


        Usuario nuevo = new Usuario();
        nuevo.setCorreo(correo);
        nuevo.setContrasena(contrasenia);
        nuevo.setRol(rolSeleccionado);
        nuevo.setEstado(1); // activo por defecto

        UsuarioDao dao = new UsuarioDao();
        boolean exito = dao.createUsuario(nuevo);

        if (exito) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registro Exitoso");
            alert.setHeaderText(null);
            alert.setContentText("Se ha registrado un nuevo usuario.");
            alert.showAndWait();
            if (onUsuarioCreado != null) {
                onUsuarioCreado.run();
            }
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
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        }
    }
}
