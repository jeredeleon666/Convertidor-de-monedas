package bodoque.imagina.convertir_monedas.util.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.text.DecimalFormat;

/**
 * Controlador para manejar la visualización de un item de historial de conversión.
 */
public class HistorialItemController {
    @FXML
    private Label labelFecha;

    @FXML
    private ImageView imageOrigen;

    @FXML
    private Label labelMontoOrigen;

    @FXML
    private Label labelMonedaOrigen;

    @FXML
    private ImageView imageDestino;

    @FXML
    private Label labelMontoDestino;

    @FXML
    private Label labelMonedaDestino;

    @FXML
    private Label labelTasaCambio;

    public HistorialItemController(){
        ;
    }

    /**
     * Configura el item con los datos de la conversión.
     *
     * @param fecha Fecha y hora de la conversión
     * @param monedaOrigen Nombre de la moneda de origen
     * @param monedaDestino Nombre de la moneda de destino
     * @param montoOrigen Monto original
     * @param montoDestino Monto convertido
     * @param imagenOrigen Imagen de la bandera de la moneda de origen
     * @param imagenDestino Imagen de la bandera de la moneda de destino
     */
    public void setItem(String fecha, String monedaOrigen, String monedaDestino,
                        double montoOrigen, double montoDestino,
                        Image imagenOrigen, Image imagenDestino, String tasaCambio) {
        // Formato para números decimales
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Establecer la fecha
        labelFecha.setText(fecha);

        // Establecer información de la moneda de origen
        imageOrigen.setImage(imagenOrigen);
        labelMontoOrigen.setText(df.format(montoOrigen));
        labelMonedaOrigen.setText(monedaOrigen);

        // Establecer información de la moneda de destino
        imageDestino.setImage(imagenDestino);
        labelMontoDestino.setText(df.format(montoDestino));
        labelMonedaDestino.setText(monedaDestino);

        labelTasaCambio.setText(tasaCambio);
    }
}