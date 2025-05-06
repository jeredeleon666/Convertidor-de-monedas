package bodoque.imagina.convertir_monedas;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ConversorApplication extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(ConversorApplication.class.getResource("ventana_principal.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 600);

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);
        stage.show();

    }




    public static void main(String[] args) {
        launch();
    }
}