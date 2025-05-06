module bodoque.imagina.convertir_monedas {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.fasterxml.jackson.databind;
    requires java.sql;
    requires java.prefs;


    opens bodoque.imagina.convertir_monedas to javafx.fxml;
    exports bodoque.imagina.convertir_monedas;
    exports bodoque.imagina.convertir_monedas.util.Controllers;
    opens bodoque.imagina.convertir_monedas.util.Controllers to javafx.fxml;
}