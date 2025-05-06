package bodoque.imagina.convertir_monedas.util.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para el componente de ítem personalizado.
 * Maneja la configuración de texto, imagen y indicador de estado.
 */
public class ItemController implements Initializable {

    @FXML
    private ImageView itemIcon;

    @FXML
    private Label itemText;

    @FXML
    private Rectangle statusIndicator;

    public ItemController(){
        ;
    }

    /**
     * Constructor que inicializa el ítem con una imagen y texto.
     *
     * @param image Imagen para el ícono
     * @param text  Texto para el ítem
     */
    public ItemController(Image image, String text) {
        setItem(image, text);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización por defecto si es necesaria
    }

    /**
     * Configura el ítem con una imagen desde una ruta y texto.
     *
     * @param iconPath Ruta a la imagen que se mostrará como ícono
     * @param text     Texto a mostrar para este ítem
     */
    public void setItem(String iconPath, String text) {
        try {
            Image icon = new Image(iconPath);
            itemIcon.setImage(icon);
        } catch (Exception e) {
            System.err.println("Error al cargar la imagen: " + e.getMessage());
        }
        itemText.setText(text);
    }

    /**
     * Configura el ítem con una imagen precreada y texto.
     *
     * @param icon Imagen que se mostrará como ícono
     * @param text Texto a mostrar para este ítem
     */
    public void setItem(Image icon, String text) {
        itemIcon.setImage(icon);
        itemText.setText(text);
    }

    /**
     * Establece la visibilidad del indicador de estado.
     *
     * @param visible true para mostrar el indicador, false para ocultarlo
     */
    public void setStatusIndicatorVisible(boolean visible) {
        statusIndicator.setVisible(visible);
        statusIndicator.setManaged(visible);
    }

    /**
     * Cambia el color del indicador de estado.
     *
     * @param colorHex Color en formato hexadecimal (por ejemplo, "#4CAF50")
     */
    public void setStatusIndicatorColor(String colorHex) {
        statusIndicator.setFill(javafx.scene.paint.Color.web(colorHex));
    }

    /**
     * Obtiene el texto actual del ítem.
     *
     * @return El texto actual del ítem
     */
    public String getItemText() {
        return itemText.getText();
    }

    /**
     * Obtiene la imagen actual del ítem.
     *
     * @return La imagen actual del ítem
     */
    public Image getItemIcon() {
        return itemIcon.getImage();
    }
}