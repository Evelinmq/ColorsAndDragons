package com.example.integradora.controllers;

import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.dao.EmpleadoDao;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateEmpleadoController implements Initializable {

    @FXML
    private TextField nombre;

    @FXML
    private TextField rfc;

    private Empleado empleado;
    private String rfcViejo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
        this.rfcViejo = empleado.getRfc();

        nombre.setText(empleado.getNombre());
        rfc.setText(empleado.getRfc());
    }

    @FXML
    public void updateEmpleado(ActionEvent event) {

        String nuevoNombre = nombre.getText();
        String nuevoRfc = rfc.getText();


        empleado.setNombre(nuevoNombre);
        empleado.setRfc(nuevoRfc);


        EmpleadoDao dao = new EmpleadoDao();
        if (dao.updateEmpleado(rfcViejo, empleado)) {
            System.out.println("Empleado actualizado correctamente.");
        }


        Stage ventana = (Stage) nombre.getScene().getWindow();
        ventana.close();
    }
}
