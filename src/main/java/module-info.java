module com.example.integradora {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ucp;


    opens com.example.integradora to javafx.fxml;
    exports com.example.integradora;
}