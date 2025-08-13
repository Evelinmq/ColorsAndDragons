package com.example.integradora.controllers;

import com.example.integradora.Main;
import com.example.integradora.modelo.Edificio;
import com.example.integradora.modelo.Puesto;
import com.example.integradora.modelo.dao.PuestoDao;
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


public class PuestoController implements Initializable {

    @FXML
    private TextField nombrePuesto;
    @FXML
    private TableView<Puesto> tablaPuesto;
    @FXML
    TableColumn<Puesto, String> tablaPuestoNombre;
    @FXML
    private AnchorPane padrePuesto;
    @FXML
    private Button resguardo, bienes, empleados, espacio, unidad, edificio, usuario,puesto;

    @FXML
    private TextField textoBusquedaPuesto;
    @FXML
    private ProgressIndicator spinner;

    @FXML
    private Button botonBusquedaPuesto, eliminarPuesto, actualizarPuesto, agregar, recuperar;
    @FXML
    private ComboBox<String> filtroEstado;

    private List<Puesto> puestos = new ArrayList<>();

    private PuestoDao dao = new PuestoDao();

    ObservableList<Puesto> opcionesTabla;
    private List<Button> menuButtons;

    private Stage dialogStage;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        menuButtons = new ArrayList<>();
        menuButtons.add(resguardo);
        menuButtons.add(unidad);
        menuButtons.add(empleados);
        menuButtons.add(espacio);
        menuButtons.add(puesto);
        menuButtons.add(edificio);
        menuButtons.add(usuario);
        menuButtons.add(bienes);

        puesto.getStyleClass().add("menu-button-selected");

        // 1. Acceder a la BD
        List<Puesto> lista = dao.readPuesto();


        //Configuración columa
        tablaPuestoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        recargarTabla();

        //Habilitar botón eliminar, editar, actualizar
        tablaPuesto.setOnMouseClicked(click -> {
            if (tablaPuesto.getSelectionModel().getSelectedItem() != null && tablaPuesto.getSelectionModel().getSelectedItem().getEstado() == 0) {
                //Activa botón
                eliminarPuesto.setDisable(true);
                actualizarPuesto.setDisable(true);
                recuperar.setDisable(false);

            } else {
                eliminarPuesto.setDisable(false);
                actualizarPuesto.setDisable(false);
                recuperar.setDisable(true);
            }
        });

        opcionesTabla = FXCollections.observableArrayList(puestos);
        tablaPuesto.setItems(opcionesTabla);

        // Botón editar
        actualizarPuesto.setDisable(true);

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarPuesto.setDisable(newValue == null);
        });

        //selecionar puesto para editar
        actualizarPuesto.setOnAction(event -> {
            Puesto seleccion = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                abrirVentanaEdicionPuesto(seleccion);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Debes seleccionar un puesto para editar");
                alert.showAndWait();
            }
        });

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            eliminarPuesto.setDisable(newValue == null);
        });


        //SELECCIONAR PARA ELIMINAR
        eliminarPuesto.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarEliminar()) {
                    if (PuestoDao.deletePuesto(seleccionado.getId())) {
                        recargarTabla();

                        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                        successAlert.setTitle("Éxito");
                        successAlert.setHeaderText(null);
                        successAlert.setContentText("El puesto ha sido eliminado correctamente.");
                        successAlert.showAndWait();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un puesto para eliminar");
                alert.showAndWait();
                recargarTabla();
            }
        });

        tablaPuesto.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getEstado() == 0) {
                recuperar.setDisable(false);
            } else {
                recuperar.setDisable(true);
            }
        });

        //Regresar puesto a estado 1
        recuperar.setOnAction(event -> {
            Puesto seleccionado = tablaPuesto.getSelectionModel().getSelectedItem();
            if (seleccionado != null) {
                if (confirmarRegresar()) {
                    if (PuestoDao.regresoPuesto(seleccionado.getId())) {
                        recargarTabla();
                    }
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Aviso");
                alert.setHeaderText(null);
                alert.setContentText("Debes seleccionar un puesto");
                alert.showAndWait();
                recargarTabla();
            }
        });


        puestos = FXCollections.observableArrayList(opcionesTabla);

        // 4. Configurar el ComboBox
        ObservableList<String> estados = FXCollections.observableArrayList("Activos", "Inactivos", "Ver todos");
        filtroEstado.setItems(estados);
        filtroEstado.getSelectionModel().select("Ver todos");
        recargarTabla();

        filtroEstado.setOnAction(event -> recargarTabla());

        // Botón agregar
        agregar.setOnAction(event -> abrirVentanaRegistro());


        textoBusquedaPuesto.textProperty().addListener((obs, old, newValue) -> {
            if (newValue.trim().isEmpty()) {
                recargarTabla(); // Usa el metodo que respeta el filtro
            }
        });

    }

    private void resetAllButtons() {
        for (Button button : menuButtons) {
            button.getStyleClass().remove("menu-button-selected");
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void abrirVentanaEdicionPuesto(Puesto p) {
        //Cargar nueva vista
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/EditarPuesto.fxml"));
            Parent root = loader.load();
            UpdatePuestoController controller = loader.getController();

            if (p != null) {
                controller.setPuesto(p); // Llama al setPuesto del controlador de edición
            }

            Scene nuevaEscena = new Scene(root);
            nuevaEscena.getStylesheets().add(Main.class.getResource("/com/example/integradora/styles/styles.css").toExternalForm());

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setScene(nuevaEscena);
            stage.setTitle("Editar Puesto");
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
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/example/integradora/RegistrarPuesto.fxml"));
            Parent root = loader.load();

            Scene nuevaEscena = new Scene(root);
            nuevaEscena.getStylesheets().add(Main.class.getResource("/com/example/integradora/styles/styles.css").toExternalForm());

            // Efecto blur al fondo
            Scene escenaPrincipal = agregar.getScene();
            Parent fondo = escenaPrincipal.getRoot();
            fondo.setEffect(new BoxBlur(10, 10, 3));

            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.setOnHidden(e -> recargarTabla());
            stage.setScene(nuevaEscena);
            stage.setTitle("Registrar Puesto");
            stage.initOwner(escenaPrincipal.getWindow());
            stage.show();


            RegistrarPuestoController controller = loader.getController();
            controller.setStage(stage);
            controller.setOnPuestoCreado(() -> {
                recargarTabla();
            });


            stage.setOnHidden(e -> fondo.setEffect(null));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void registrarPuesto(ActionEvent event) {
        // Obtenemos la info del campo de texto
        String puestoV = nombrePuesto.getText().trim();
        if (puestoV.isEmpty()) return;

        Puesto nuevo = new Puesto();
        nuevo.setNombre(puestoV);
        nuevo.setEstado(1); // activo

        if (dao.createPuesto(nuevo)) {
            System.out.println("Se insertó con éxito");
        }

        nombrePuesto.setText("");
        recargarTabla();
    }

    private boolean confirmarRegresar() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar regresar");
        alert.setHeaderText(null);
        alert.setContentText("¿Deseas regresar el puesto?");
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
    private void buscarPuesto(ActionEvent event) {
        // Desactiva el botón y muestra el spinner
        botonBusquedaPuesto.setDisable(true);
        spinner.setVisible(true);

        // Captura texto de búsqueda
        String texto = textoBusquedaPuesto.getText().trim().toLowerCase();
        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        // Simula búsqueda en segundo plano
        Task<List<Puesto>> tareaBusqueda = new Task<>() {
            @Override
            protected List<Puesto> call() {
                List<Puesto> lista = dao.readPuesto(); // Carga todos los puestos
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
                List<Puesto> resultado = getValue();
                tablaPuesto.setItems(FXCollections.observableArrayList(resultado));
                tablaPuesto.refresh();

                spinner.setVisible(false);
                botonBusquedaPuesto.setDisable(false);
            }

            @Override
            protected void failed() {
                spinner.setVisible(false);
                botonBusquedaPuesto.setDisable(false);
            }
        };

        Thread hilo = new Thread(tareaBusqueda);
        hilo.setDaemon(true);
        hilo.start();
    }


    private void recargarTabla() {
        List<Puesto> lista = dao.readPuesto();
        ObservableList<Puesto> listaObservable = FXCollections.observableArrayList(lista);

        String filtro = filtroEstado.getSelectionModel().getSelectedItem();

        if (filtro != null) {
            switch (filtro) {
                case "Activos":
                    tablaPuesto.setItems(listaObservable.filtered(p -> p.getEstado() == 1));
                    break;
                case "Inactivos":
                    tablaPuesto.setItems(listaObservable.filtered(p -> p.getEstado() == 0));
                    break;
                case "Ver todos":
                    tablaPuesto.setItems(listaObservable);
                    break;
                default:
                    tablaPuesto.setItems(listaObservable); // fallback
            }
        } else {
            tablaPuesto.setItems(listaObservable); // si no hay filtro seleccionado aún
        }

        tablaPuesto.refresh();
    }


    //Botones cambiar a vistas
    @FXML
    protected void irResguardo() {
        resetAllButtons();
        resguardo.getStyleClass().add("menu-button-selected");
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
        resetAllButtons();
        bienes.getStyleClass().add("menu-button-selected");
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
        resetAllButtons();
        empleados.getStyleClass().add("menu-button-selected");
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
        resetAllButtons();
        espacio.getStyleClass().add("menu-button-selected");
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
    protected void irUnidad() {
        resetAllButtons();
        unidad.getStyleClass().add("menu-button-selected");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/com/example/integradora/VistaUnidadAdm.fxml"));
            Parent newRoot = fxmlLoader.load();
            Stage stage = (Stage) unidad.getScene().getWindow();
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
        resetAllButtons();
        edificio.getStyleClass().add("menu-button-selected");
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
        resetAllButtons();
        usuario.getStyleClass().add("menu-button-selected");
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
    private void filtrarPorEstado() {
        String opcion = filtroEstado.getValue();
        List<Puesto> lista = new ArrayList<>();

        switch (opcion) {
            case "Activos":
                lista = dao.readPuestoPorEstado(1);
                break;
            case "Inactivos":
                lista = dao.readPuestoPorEstado(0);
                break;
            case "Ver todos":
                lista = dao.readTodosPuestos();
                break;
        }

        tablaPuesto.setItems(FXCollections.observableList(lista));
        tablaPuesto.refresh();

        // Deshabilitar botón de restaurar al cambiar el filtro
        recuperar.setDisable(true);
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
