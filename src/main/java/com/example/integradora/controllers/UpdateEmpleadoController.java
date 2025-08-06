package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.PuestoDao;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateEmpleadoController implements Initializable {

    @FXML
    private TextField nombre;
    @FXML
    private TextField apellidoPaterno;
    @FXML
    private TextField apellidoMaterno;
    @FXML
    private ComboBox<Puesto> cbPuesto;
    @FXML
    private ComboBox<UnidadAdministrativa> cbUnidadAdministrativa;
    @FXML
    private TextField rfc;

    private Empleado empleadoEdicion;
    private String rfcViejo;
    private Stage stage;
    private Runnable onEmpleadoActualizado;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setOnEmpleadoActualizado(Runnable onEmpleadoActualizado) {
        this.onEmpleadoActualizado = onEmpleadoActualizado;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<Puesto> puestos = PuestoDao.readPuestosActivos();
        cbPuesto.setItems(javafx.collections.FXCollections.observableArrayList(puestos));
        cbPuesto.setConverter(new StringConverter<Puesto>() {
            @Override
            public String toString(Puesto puesto) {
                return puesto != null ? puesto.getNombre() : "";
            }

            @Override
            public Puesto fromString(String string) {
                return cbPuesto.getItems().stream()
                        .filter(p -> p.getNombre().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        try {

            List<UnidadAdministrativa> unidades = UnidadAdministrativaDao.readUnidadesActivas();
            cbUnidadAdministrativa.setItems(javafx.collections.FXCollections.observableArrayList(unidades));
            cbUnidadAdministrativa.setConverter(new StringConverter<UnidadAdministrativa>() {
                @Override
                public String toString(UnidadAdministrativa unidad) {
                    return unidad != null ? unidad.getNombre() : "";
                }

                @Override
                public UnidadAdministrativa fromString(String string) {
                    return cbUnidadAdministrativa.getItems().stream()
                            .filter(u -> u.getNombre().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


        rfc.setEditable(false);
    }

    public void setEmpleado(Empleado empleado) {
        this.empleadoEdicion = empleado;
        this.rfcViejo = empleado.getRfc();

        if (empleadoEdicion != null) {
            llenarCampos();
        }

    }
    @FXML
    public void updateEmpleado(ActionEvent event) {
        if (empleadoEdicion == null) {
            showAlert(Alert.AlertType.ERROR, "Error de Edición", "No se ha seleccionado ningún empleado para actualizar.");
            return;
        }

        String nuevoNombre = nombre.getText().trim();
        String nuevoApellidoPaterno = apellidoPaterno.getText().trim();
        String nuevoApellidoMaterno = apellidoMaterno.getText().trim();
        Puesto puestoSeleccionado = cbPuesto.getSelectionModel().getSelectedItem();
        UnidadAdministrativa unidadSeleccionada = cbUnidadAdministrativa.getSelectionModel().getSelectedItem();

        if (nuevoNombre.isEmpty() || nuevoApellidoPaterno.isEmpty() || nuevoApellidoMaterno.isEmpty() ||
                puestoSeleccionado == null || unidadSeleccionada == null) {
            showAlert(Alert.AlertType.WARNING, "Campos Incompletos", "Todos los campos (nombre, apellidos, puesto, unidad) son obligatorios.");
            return;
        }

        empleadoEdicion.setNombre(nuevoNombre);
        empleadoEdicion.setApellidoPaterno(nuevoApellidoPaterno);
        empleadoEdicion.setApellidoMaterno(nuevoApellidoMaterno);
        empleadoEdicion.setIdPuesto(puestoSeleccionado.getId());
        empleadoEdicion.setIdUnidadAdministrativa(unidadSeleccionada.getId());
        empleadoEdicion.setPuesto(puestoSeleccionado);
        empleadoEdicion.setUnidadAdministrativa(unidadSeleccionada);

        EmpleadoDao dao = new EmpleadoDao();

        if (dao.updateEmpleado(rfcViejo, empleadoEdicion)) {
            showAlert(Alert.AlertType.INFORMATION, "Éxito", "Empleado actualizado correctamente.");
            if (onEmpleadoActualizado != null) {
                onEmpleadoActualizado.run();
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el empleado.");
        }
        cerrarVentana(event);
    }

    private void llenarCampos() {
        if (empleadoEdicion != null) {
            nombre.setText(empleadoEdicion.getNombre());
            apellidoPaterno.setText(empleadoEdicion.getApellidoPaterno());
            apellidoMaterno.setText(empleadoEdicion.getApellidoMaterno());
            rfc.setText(empleadoEdicion.getRfc());


            if (empleadoEdicion.getPuesto() != null) {
                cbPuesto.getSelectionModel().select(empleadoEdicion.getPuesto());
            } else {
                cbPuesto.getItems().stream()
                        .filter(p -> p.getId() == empleadoEdicion.getIdPuesto())
                        .findFirst()
                        .ifPresent(p -> cbPuesto.getSelectionModel().select(p));
            }

            if (empleadoEdicion.getUnidadAdministrativa() != null) {
                cbUnidadAdministrativa.getSelectionModel().select(empleadoEdicion.getUnidadAdministrativa());
            } else {
                int idUnidadAdministrativaEmpleado = empleadoEdicion.getIdUnidadAdministrativa();
                cbUnidadAdministrativa.getItems().stream()
                        .filter(u -> u.getId() == idUnidadAdministrativaEmpleado)
                        .findFirst()
                        .ifPresent(u -> cbUnidadAdministrativa.getSelectionModel().select(u));
            }
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

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}