package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.modelo.dao.*;
import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.ResguardoBien;
import com.example.integradora.modelo.UnidadAdministrativa;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistroResguardoController  implements Initializable {

    @FXML
    private ComboBox<Bien> comboBoxBusqueda;
    @FXML
    private Button buscar, botonEliminar, cancelar, guardar;
    @FXML
    private TableView<Bien> tabla;
    @FXML
    private TableColumn<Bien, String> codigo, descripcion, marca, modelo, serie;
    @FXML
    private DatePicker fecha;
    @FXML
    private ComboBox<Empleado> empleado;
    @FXML
    private ComboBox<Espacio> espacio;

    private List<Bien> lista;

    ObservableList<Bien> opcionesTabla;

    private ObservableList<Bien> bienesDisponibles;

    private BienDao bienDao;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        bienDao = new BienDao();
        lista = new ArrayList<>();

        guardar.setOnAction(e -> guardarResguardo());
        cancelar.setOnAction(e -> cerrarVentana());

        codigo.setCellValueFactory(new PropertyValueFactory<>("bien_codigo"));
        descripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        modelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        marca.setCellValueFactory(new PropertyValueFactory<>("marca"));
        serie.setCellValueFactory(new PropertyValueFactory<>("serie"));

        opcionesTabla = FXCollections.observableArrayList(lista);
        tabla.setItems(opcionesTabla);


        comboBoxBusqueda.setConverter(new StringConverter<Bien>() {
            @Override
            public String toString(Bien bien) {
                return (bien == null) ? "" : bien.getBien_codigo();
            }

            @Override
            public Bien fromString(String string) {
                for (Bien b : comboBoxBusqueda.getItems()) {
                    if (b.getBien_codigo().equalsIgnoreCase(string)) {
                        return b;
                    }
                }
                return null;
            }
        });


        comboBoxBusqueda.setItems(opcionesTabla);
        comboBoxBusqueda.setEditable(true);

        comboBoxBusqueda.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = comboBoxBusqueda.getEditor();
            final String selected = String.valueOf(comboBoxBusqueda.getSelectionModel().getSelectedItem());

            if (selected == null || !selected.equals(editor.getText())) {
                filterComboBoxItems(comboBoxBusqueda, bienesDisponibles, newVal); // Cambiado a bienesDisponibles
            }
        });

        tabla.setOnMouseClicked(click -> {
            if (tabla.getSelectionModel().getSelectedItem() != null) {
                botonEliminar.setDisable(false);
            }else{
                botonEliminar.setDisable(true);
            }
        });

        cargarBienes();
        cargarCombos();

    }

    public void getSeleccion(ActionEvent event){
        Bien seleccion = comboBoxBusqueda.getSelectionModel().getSelectedItem();
        if(seleccion != null && seleccion.getEstado() == 1){
            if(!lista.contains(seleccion)) { // Evita que se dupliquen los datos
                lista.add(seleccion);
            }
        }
        opcionesTabla.setAll(lista);
        tabla.refresh();

        comboBoxBusqueda.setItems(bienesDisponibles);
    }

    private void filterComboBoxItems(ComboBox<Bien> comboBox, ObservableList<Bien> originalItems, String filter) {
        if(filter == null || filter.isEmpty()){
            comboBox.setItems(originalItems);
            return;
        }

        ObservableList<Bien> filteredList = FXCollections.observableArrayList();
        for (Bien item : originalItems) {
            if (item.getBien_codigo().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(item);
            }
        }
        comboBox.setItems(filteredList);
        comboBox.show();
    }

    private void cargarBienes(){
        List<Bien> todosLosBienes = bienDao.readBien();
        bienesDisponibles = FXCollections.observableArrayList(todosLosBienes);
        comboBoxBusqueda.setItems(bienesDisponibles);
    }

    @FXML
    public void eliminarSeleccion(){
        Bien seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            lista.remove(seleccionado);
            opcionesTabla.setAll(lista);
        }
        tabla.getSelectionModel().clearSelection();
        botonEliminar.setDisable(true);
    }

    private void cargarCombos() {
        List<Empleado> empleados = EmpleadoDao.readEmpleadosActivos();
        List<Espacio> espacios = EspacioDao.readEspaciosActivos();

        empleado.setItems(FXCollections.observableArrayList(empleados));
        espacio.setItems(FXCollections.observableArrayList(espacios));

        empleado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Empleado object) {
                if (object == null) {
                    return "";
                } else {
                    return object.getNombre() + " " + object.getApellidoPaterno() + " " + object.getApellidoMaterno();
                }
            }

            @Override
            public Empleado fromString(String string) {
                return empleado.getItems().stream()
                        .filter(e -> (e.getNombre() + " " + e.getApellidoPaterno() + " " + e.getApellidoMaterno()).equals(string))
                        .findFirst().orElse(null);
            }
        });

        espacio.setConverter(new StringConverter<>() {
            @Override
            public String toString(Espacio object) {
                if (object == null) {
                    return "";
                } else {
                    return object.getNombre();
                }
            }

            @Override
            public Espacio fromString(String string) {
                return espacio.getItems().stream()
                        .filter(e -> e.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    @FXML
    private void guardarResguardo() {
        LocalDate fechaSeleccionada = fecha.getValue();
        Empleado emp = empleado.getValue();
        Espacio esp = espacio.getValue();

        if (fechaSeleccionada == null || emp == null || esp == null || lista.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,"ERROR", "Debes completar todos los campos y agregar al menos un bien");
            return;
        }

        Resguardo nuevoResguardo = new Resguardo();
        nuevoResguardo.setFecha(Date.valueOf(fechaSeleccionada).toLocalDate());
        nuevoResguardo.setEmpleado(emp);
        nuevoResguardo.setEspacio(esp);
        nuevoResguardo.setEstado(1);

        int idGenerado = ResguardoDao.insertarResguardo(nuevoResguardo);

        if (idGenerado > 0) {
            nuevoResguardo.setId(idGenerado);

            List<ResguardoBien> listaResguardoBien = new ArrayList<>();
            Edificio edificio = esp.getEdificio();
            UnidadAdministrativa unidad = emp.getUnidadAdministrativa();
            Puesto puesto = emp.getPuesto();

            for (Bien bien : lista) {
                ResguardoBien rb = new ResguardoBien();
                rb.setResguardo(nuevoResguardo);
                rb.setBien(bien);
                rb.setEspacio(esp);
                rb.setEdificio(edificio);
                rb.setEmpleado(emp);
                rb.setUnidad(unidad);
                rb.setPuesto(puesto);

                listaResguardoBien.add(rb);
            }

            ResguardoBienDao resguardoBienDao = new ResguardoBienDao();
            if (resguardoBienDao.insertarResguardoBien(listaResguardoBien)) {
                mostrarAlerta(Alert.AlertType.INFORMATION,"Éxito", "Resguardo registrado correctamente.");
                limpiarFormulario();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR,"ERROR", "Se registró el resguardo, pero no se pudieron registrar los bienes.");
            }

        } else {
            mostrarAlerta(Alert.AlertType.ERROR,"ERROR", "No se pudo registrar el resguardo.");
        }
    }

    private void cerrarVentana() {
        Stage ventana = (Stage) tabla.getScene().getWindow();
        ventana.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        fecha.setValue(null);
        empleado.getSelectionModel().clearSelection();
        espacio.getSelectionModel().clearSelection();
        lista.clear();
        tabla.getItems().clear();
        cargarBienes();
    }

}
