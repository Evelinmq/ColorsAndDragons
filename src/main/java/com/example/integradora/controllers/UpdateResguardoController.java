package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.modelo.dao.ResguardoBienDao;
import com.example.integradora.modelo.dao.ResguardoDao;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.EspacioDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class UpdateResguardoController {
    @FXML
    private DatePicker datePickerFecha;

    @FXML
    private ComboBox<Empleado> comboEmpleado;

    @FXML
    private ComboBox<Espacio> comboEspacio;

    @FXML
    private TableView<Bien> tablaBienes;
    @FXML
    private TableColumn<Bien, String> codigo, descripcion, marca, modelo, serie;

    // ID del resguardo actual
    private int idResguardoActual;

    public void inicializarDatos(int idResguardo) {
        this.idResguardoActual = idResguardo;

        cargarCombos();

        Resguardo resguardo = ResguardoDao.obtenerPorId(idResguardo);
        if (resguardo != null) {
            // Establece los valores
            datePickerFecha.setValue(resguardo.getFecha());

            // Selecciona el empleado en el ComboBox
            comboEmpleado.getItems().stream()
                    .filter(e -> e.getRfc().equals(resguardo.getEmpleado().getRfc()))
                    .findFirst()
                    .ifPresent(comboEmpleado::setValue);

            // Selecciona el espacio en el ComboBox
            comboEspacio.getItems().stream()
                    .filter(esp -> esp.getId() == resguardo.getEspacio().getId())
                    .findFirst()
                    .ifPresent(comboEspacio::setValue);
        }

        cargarBienes(idResguardo);
    }


    private void cargarCombos() {
        comboEmpleado.setItems(FXCollections.observableArrayList(EmpleadoDao.readEmpleados()));
        comboEspacio.setItems(FXCollections.observableArrayList(EspacioDao.readTodosEspacios()));
    }

    private void cargarBienes(int idResguardo) {
        List<Bien> bienes = ResguardoBienDao.obtenerBienesPorResguardo(idResguardo);
        tablaBienes.setItems(FXCollections.observableArrayList(bienes));
    }

}
