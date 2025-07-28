package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import com.example.integradora.modelo.Resguardo;
import com.example.integradora.modelo.dao.ResguardoDao;
import com.example.integradora.modelo.dao.ResguardoBienDao;
import com.example.integradora.modelo.dao.EmpleadoDao;
import com.example.integradora.modelo.dao.EspacioDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
import java.sql.Date;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){

        lista = new ArrayList<Bien>();

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
                return (bien == null) ? "" : bien.getBien_codigo(); // Lo que se muestra al usuario
            }

            @Override
            public Bien fromString(String string) {
                for (Bien b : comboBoxBusqueda.getItems()) {
                    if (b.getBien_codigo().equalsIgnoreCase(string)) {
                        return b;
                    }
                }
                return null; // Si no se encuentra uno, no lo convierte
            }
        });


        comboBoxBusqueda.setItems(opcionesTabla);
        comboBoxBusqueda.setEditable(true); // Activa el campo de búsqueda

        // Búsqueda en tiempo real (opcional para filtrado más avanzado)
        comboBoxBusqueda.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = comboBoxBusqueda.getEditor();
            final String selected = String.valueOf(comboBoxBusqueda.getSelectionModel().getSelectedItem());

            // Si el usuario está escribiendo, filtra opciones
            if (selected == null || !selected.equals(editor.getText())) {
                filterComboBoxItems(comboBoxBusqueda, opcionesTabla, newVal);
            }
        });

        tabla.setOnMouseClicked(click -> {
            if (tabla.getSelectionModel().getSelectedItem() != null) {
                //activar el boton de borrado
                botonEliminar.setDisable(false);

            }else{
                botonEliminar.setDisable(true);
            }
        });

        cargarCombos();

    }



    public void getSeleccion(ActionEvent event){
        Bien seleccion = comboBoxBusqueda.getSelectionModel().getSelectedItem();
        if(seleccion != null && seleccion.getEstado() == 1){
            lista.add(seleccion);
        }

        opcionesTabla = FXCollections.observableArrayList(lista);
        tabla.setItems(opcionesTabla);
        tabla.refresh();

        for (Bien bien : lista){
            System.out.println(bien);
        }

    }

    private void filterComboBoxItems(ComboBox<Bien> comboBox, ObservableList<Bien> originalItems, String filter) {
        ObservableList<Bien> filteredList = FXCollections.observableArrayList();

        for (Bien item : originalItems) {
            if (item.getBien_codigo().toLowerCase().contains(filter.toLowerCase())) {
                filteredList.add(item);
            }
        }

        comboBox.setItems(filteredList);
        comboBox.show(); // Mantenerlo abierto mientras escribe
    }

    @FXML
    public void eliminarSeleccion(){
        if (tabla.getSelectionModel().getSelectedItem() != null) {
            Bien seleccionado = tabla.getSelectionModel().getSelectedItem();
            tabla.getItems().remove(seleccionado);
        }
        tabla.getSelectionModel().clearSelection();
        botonEliminar.setDisable(true);
    }

    //CARGAR EMPLEADO Y ESPACIO
    private void cargarCombos() {
        List<Empleado> empleados = EmpleadoDao.readEmpleados();
        List<Espacio> espacios = EspacioDao.readTodosEspacios();

        empleado.setItems(FXCollections.observableArrayList(empleados));
        espacio.setItems(FXCollections.observableArrayList(espacios));

        empleado.setConverter(new StringConverter<>() {
            @Override
            public String toString(Empleado object) {
                return (object == null) ? "" : object.getNombre();
            }

            @Override
            public Empleado fromString(String string) {
                return empleado.getItems().stream()
                        .filter(e -> e.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });

        espacio.setConverter(new StringConverter<>() {
            @Override
            public String toString(Espacio object) {
                return (object == null) ? "" : object.getNombre();
            }

            @Override
            public Espacio fromString(String string) {
                return espacio.getItems().stream()
                        .filter(e -> e.getNombre().equals(string))
                        .findFirst().orElse(null);
            }
        });
    }

    //GUARDA EL RESGUARDO CON SUS RESPECTIVOS BIENES
    @FXML
    public void guardarResguardo(ActionEvent event) {
        LocalDate fechaSeleccionada = fecha.getValue();
        Empleado emp = empleado.getValue();
        Espacio esp = espacio.getValue();

        if (fechaSeleccionada == null || emp == null || esp == null || lista.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,"ERROR", "Debes completar todos los campos y agregar al menos un bien");
            return;
        }

        Resguardo nuevo = new Resguardo();
        nuevo.setFecha(Date.valueOf(fechaSeleccionada).toLocalDate());
        nuevo.setEmpleado(emp);
        nuevo.setEspacio(esp);
        nuevo.setEstado(1); // Está activo

        int idGenerado = ResguardoDao.insertarResguardo(nuevo); // regresa ID generado

        if (idGenerado > 0) {
            for (Bien bien : lista) {
                ResguardoBienDao.insertarResguardoBien(idGenerado, bien.getBien_codigo());
            }
            mostrarAlerta(Alert.AlertType.INFORMATION,"Éxito", "Resguardo registrado correctamente.");
            limpiarFormulario();
        } else {
            mostrarAlerta(Alert.AlertType.ERROR,"ERROR", "No se pudo registrar el resguardo.");
        }
    }


    //ALERTA
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    //LIMPIAR EL FORMULARIO
    private void limpiarFormulario() {
        fecha.setValue(null);
        empleado.getSelectionModel().clearSelection();
        espacio.getSelectionModel().clearSelection();
        lista.clear();
        tabla.getItems().clear();
    }


}
