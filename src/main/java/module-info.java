module com.example.integradora {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires ucp;
    requires ojdbc8;
    requires java.desktop;
    requires net.sf.jasperreports.core;
    requires jbcrypt;

    opens com.example.integradora to javafx.fxml;
    exports com.example.integradora;
    opens com.example.integradora.controllers to javafx.fxml;
    exports com.example.integradora.modelo;


}