package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RegistrarEmpleadoController {

    @FXML public TextField txfNombre;
    @FXML public TextField txfApellidoP;
    @FXML public TextField txfApellidoM;
    @FXML public TextField txfRfc;
    @FXML public ComboBox <String> cbPuesto;
    @FXML public ComboBox <String> cbUnidadAdministrativa;
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

    public void initialize() {
        var items = FXCollections.observableArrayList("Profesor de idiomas", "Profesor por hora", "Profesor por tiempo completo", "Coordinador decarrera");
        cbPuesto.setItems(items);
        cbUnidadAdministrativa.setItems(getNombreUnidadAdministrativa());
    }

    private ObservableList<String> getNombreUnidadAdministrativa(){
        ArrayList<UnidadAdministrativa> auxliar = (ArrayList<UnidadAdministrativa>) UnidadAdministrativaDao.readTodosUnidades();
        ObservableList<String> items = FXCollections.observableArrayList();
        for(UnidadAdministrativa i: auxliar){
            items.add(i.getNombre());

        }

        return items;
    }


    public void setOnEmpleadoCreado(Runnable onEmpleadoCreado) {
        this.onEmpleadoCreado = onEmpleadoCreado;
    }

    @FXML
    private void guardarEmpleado(ActionEvent event) {
        System.out.println("Guardando Empleado");

        String nombre = txfNombre.getText().trim();
        String apellidoP = txfApellidoP.getText().trim();
        String apellidoM = txfApellidoM.getText().trim();
        String rfc = txfRfc.getText().trim();


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
