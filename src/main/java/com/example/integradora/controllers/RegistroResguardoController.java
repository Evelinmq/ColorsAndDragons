package com.example.integradora.controllers;

import com.example.integradora.modelo.Bien;
import com.example.integradora.modelo.Empleado;
import com.example.integradora.modelo.Espacio;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;

import java.net.URL;
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


    //CONFIGURAR EL DATEPICKER PARA LA FECHA

    //CONFIGURAR EL COMBOBOX PARA EMPLEADO

    //CONFIGURAR EL COMBOBOX PARA ESPACIO
}
