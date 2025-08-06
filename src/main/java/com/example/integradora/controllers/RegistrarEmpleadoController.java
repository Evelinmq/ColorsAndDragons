package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.PuestoDao;
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
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrarEmpleadoController {

    @FXML public TextField txfNombre;
    @FXML public TextField txfApellidoP;
    @FXML public TextField txfApellidoM;
    @FXML public TextField txfRfc;
    @FXML public ComboBox <Puesto> cbPuesto;
    @FXML public ComboBox <UnidadAdministrativa> cbUnidadAdministrativa;
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


    public void initialize() throws IOException {
        List<Puesto> puestos = PuestoDao.readPuestosActivos();
        cbPuesto.setItems(FXCollections.observableArrayList(puestos));

        cbPuesto.setConverter(new StringConverter<Puesto>() {
            @Override
            public String toString(Puesto puesto) {
                return puesto != null ? puesto.getNombre() : "";
            }

            @Override
            public Puesto fromString(String string) {
                // No es estrictamente necesario para este caso de uso si no permites edición manual o búsqueda
                return cbPuesto.getItems().stream()
                        .filter(p -> p.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });


        // Cargar Unidades Administrativas
        List<UnidadAdministrativa> unidades = UnidadAdministrativaDao.readUnidadesActivas(); // Usas este método, asumo que devuelve List<UnidadAdministrativa>
        cbUnidadAdministrativa.setItems(FXCollections.observableArrayList(unidades));

        cbUnidadAdministrativa.setConverter(new StringConverter<UnidadAdministrativa>() {
            @Override
            public String toString(UnidadAdministrativa unidad) {
                return unidad != null ? unidad.getNombre() : "";
            }

            @Override
            public UnidadAdministrativa fromString(String string) {
                return cbUnidadAdministrativa.getItems().stream()
                        .filter(u -> u.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });
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

        Puesto puestoSeleccionado = cbPuesto.getSelectionModel().getSelectedItem();
        UnidadAdministrativa unidadSeleccionada = cbUnidadAdministrativa.getSelectionModel().getSelectedItem();

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
        nuevo.setEstado(1);

        nuevo.setIdPuesto(puestoSeleccionado.getId());
        nuevo.setIdUnidadAdministrativa(unidadSeleccionada.getId());

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
