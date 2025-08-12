package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.UnidadAdministrativa;
import com.example.integradora.modelo.dao.UnidadAdministrativaDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;


public class UnidadController implements Initializable {

    @FXML
    private TextField nombreUnidad;
    @FXML
    private TableView<UnidadAdministrativa> tablaUnidad;
    @FXML
    TableColumn<UnidadController, String> tablaUnidadNombre;
    @FXML
    private AnchorPane padreUnidad;
    @FXML
    private Button resguardo, bienes, empleados, espacio, puesto, edificio, usuario;

    @FXML
    private TextField textoBusquedaUnidad;
    @FXML
    private ProgressIndicator spinner;

    @FXML
    private Button botonBusquedaUnidad, eliminarUnidad, actualizarUnidad, agregar, recuperar;
    @FXML
    private ComboBox<String> filtroEstado;

    private List<UnidadAdministrativa> unidades = new ArrayList<>();

    private UnidadAdministrativaDao dao = new UnidadAdministrativaDao();

    ObservableList<UnidadAdministrativa> opcionesTabla;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        // 1. Acceder a la BD
        List<UnidadAdministrativa> lista = dao.readUnidad();

        //Configuración columa
        tablaUnidadNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        recargarTabla();

        //Habilitar botón eliminar, editar, actualizar
        tablaUnidad.setOnMouseClicked(click -> {
            if (tablaUnidad.getSelectionModel().getSelectedItem() != null && tablaUnidad.getSelectionModel().getSelectedItem().getEstado() == 0) {
                //Activa botón
                actualizarUnidad.setDisable(true);
                eliminarUnidad.setDisable(true);
                recuperar.setDisable(false);

            } else {
                eliminarUnidad.setDisable(false);
                actualizarUnidad.setDisable(false);
                recuperar.setDisable(true);
            }
        });

        opcionesTabla = FXCollections.observableArrayList(unidades);
        tablaUnidad.setItems(opcionesTabla);

        // Botón editar
        actualizarUnidad.setDisable(true);

        tablaUnidad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarUnidad.setDisable(newValue == null);
        });

        //selecionar unidad para editar
        actualizarUnidad.setOnAction(event -> {
            UnidadAdministrativa seleccion = tablaUnidad.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicionUnidad(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar una unidad administrativa para editar");
                alert.showAndWait();
            }
        });

        tablaUnidad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            eliminarUnidad.setDisable(newValue == null);
        });


        //SELECCIONAR PARA ELIMINAR
        eliminarUnidad.setOnAction(event -> {
            UnidadAdministrativa seleccionado = tablaUnidad.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (UnidadAdministrativaDao.deleteUnidad(seleccionado.getId())) {
                        recargarTabla();

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Éxito");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("La unidad administrativa ha sido eliminada correctamente.");
                        successAlert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar una unidad administrativa para eliminar");
                alert.showAndWait();
                recargarTabla();
            }
        });

        tablaUnidad.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getEstado() == 0) {
                recuperar.setDisable(false);
            } else {
                recuperar.setDisable(true);
            }
        });

        //Regresar unidad a estado 1
        recuperar.setOnAction(event -> {
            UnidadAdministrativa seleccionado = tablaUnidad.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRegresar()) {
                    if (UnidadAdministrativaDao.regresoUnidad(seleccionado.getId())) {
                        recargarTabla();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar una unidad administrativa para eliminar");
                alert.showAndWait();
                recargarTabla();
            }
        });


        unidades = FXCollections.observableArrayList(opcionesTabla);

        // 4. Configurar el ComboBox
        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "Ver todos");
        filtroEstado.setItems(estados);
        filtroEstado.getSelectionModel().select("Ver todos");
        recargarTabla();

        filtroEstado.setOnAction(event -> recargarTabla());

        // Botón agregar
        agregar.setOnAction(event -> abrirVentanaRegistro());


        textoBusquedaUnidad.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                recargarTabla(); // Usa el metodo que respeta el filtro
            }
        });

    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVentanaEdicionUnidad(UnidadAdministrativa u) {
        //Cargar nueva vista
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarUnidad.fxml"));
            Parent root = loader.load();
            UpdateUnidadController controller = loader.getController();

            if (u != null) {
                controller.setUnidadAdministrativa(u); // Llama al setUnidadAdministrativa del controlador de edición
            }

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(new Scene(root));
            stage.setTitle("Editar Unidad Administrativa");
            stage.initOwner(escenaPrincipal.getWindow());
            //stage.show();

            stage.setOnHidden(e -> {
                fondo.setEffect(null); // Quita el blur
                recargarTabla();      // Recarga la tabla
            });

            stage.show();
            //stage.setOnHidden(e -> fondo.setEffect(null));
        } catch (IOException e) {
            e.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Error al abrir ventana");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("No se pudo abrir la ventana de edición. Por favor, inténtalo de nuevo.");
            errorAlert.showAndWait();
        }
    }

    private void abrirVentanaRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarUnidad.fxml"));
            Parent root = loader.load();

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(e -> recargarTabla());
            stage.setScene(new Scene(root));
            stage.setTitle("Registrar Unidad Administrativa");
            stage.initOwner(escenaPrincipal.getWindow());
            stage.show();

            RegistrarUnidadController controller = loader.getController();
            controller.setStage(stage);
            controller.setOnUnidadCreado(() -> {
                recargarTabla();
            });


            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarUnidad(ActionEvent event) {
        // Obtenemos la info del campo de texto
        String unidadV = nombreUnidad.getText().trim();
        if (unidadV.isEmpty()) return;

        UnidadAdministrativa nuevo = new UnidadAdministrativa();
        nuevo.setNombre(unidadV);
        nuevo.setEstado(1); // activo

        if (dao.createUnidad(nuevo)) {
            System.out.println("Se insertó con éxito");
        }

        nombreUnidad.setText("");
        recargarTabla();
    }

    private boolean confirmarRegresar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar regresar");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas regresar la unidad administrativa?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    private boolean confirmarEliminar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas eliminar el registro?");
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

    @FXML
    private void buscarUnidad(ActionEvent event) {
        // Desactiva el botón y muestra el spinner
        botonBusquedaUnidad.setDisable(true);
        spinner.setVisible(true);

        // Captura texto de búsqueda
        String texto = textoBusquedaUnidad.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        // Simula búsqueda en segundo plano
        Task<List<UnidadAdministrativa>> tareaBusqueda = new Task<>() {
            @Override
            protected List<UnidadAdministrativa> call() {
                List<UnidadAdministrativa> lista = dao.readUnidad();
                return lista.stream()
                        .filter(p -> {
                            boolean coincideTexto = p.getNombre().toLowerCase().contains(texto);
                            boolean coincideEstado = true;

                            if (filtro != null) {
                                switch (filtro) {
                                    case "Activos":
                                        coincideEstado = p.getEstado() == 1;
                                        break;
                                    case "Inactivos":
                                        coincideEstado = p.getEstado() == 0;
                                        break;
                                    case "Ver todos":
                                        coincideEstado = true;
                                        break;
                                }
                            }

                            return coincideTexto && coincideEstado;
                        })
                        .toList();
            }

            @Override
            protected void succeeded() {
                List<UnidadAdministrativa> resultado = getValue();
                tablaUnidad.setItems(FXCollections.observableArrayList(resultado));
                tablaUnidad.refresh();

                spinner.setVisible(false);
                botonBusquedaUnidad.setDisable(false);
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                botonBusquedaUnidad.setDisable(false);
            }
        };

        Thread hilo = new Thread(tareaBusqueda);
        hilo.setDaemon(true);
        hilo.start();
    }




    private void recargarTabla() {
        List<UnidadAdministrativa> lista = dao.readUnidad();
        ObservableList<UnidadAdministrativa> listaObservable = FXCollections.observableArrayList(lista);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        if (filtro != null) {
            switch (filtro) {
                case "Activos":
                    tablaUnidad.setItems(listaObservable.filtered(u -> u.getEstado() == 1));
                    break;
                case "Inactivos":
                    tablaUnidad.setItems(listaObservable.filtered(u -> u.getEstado() == 0));
                    break;
                case "Ver todos":
                    tablaUnidad.setItems(listaObservable);
                    break;
                default:
                    tablaUnidad.setItems(listaObservable); // fallback
            }
        } else {
            tablaUnidad.setItems(listaObservable); // si no hay filtro seleccionado aún
        }

        tablaUnidad.refresh();
    }


    //Botones cambiar a vistas
    @FXML
    protected void irResguardo() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaResguardo.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) resguardo.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irBienes() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaBienes.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) bienes.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEmpleados() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEmpleado.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) empleados.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEspacio() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEspacio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) espacio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irPuesto() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/Vistapuesto.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) puesto.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irEdificio() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaEdificio.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) edificio.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void irUsuario() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUsuario.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) usuario.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.setRoot(newRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
}

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación de Cierre de Sesión");
        alert.setHeaderText("Estás a punto de cerrar la sesión.");
        alert.setContentText("¿Estás seguro de que quieres cerrar la sesión?");


        Optional<ButtonType> result = alert.showAndWait();


        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/IniciarSesion.fxml"));
                Parent newRoot = fxmlLoader.load();
                Stage stage = (Stage) usuario.getScene().getWindow();
                Scene scene = stage.getScene();
                scene.setRoot(newRoot);


                stage.setTitle("Iniciar Sesión");
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error de carga");
                errorAlert.setHeaderText("Error al cargar la vista de inicio de sesión.");
                errorAlert.setContentText("No se pudo cargar la vista de inicio de sesión");
                errorAlert.showAndWait();
                e.printStackTrace();
            }
        }

    }
}
