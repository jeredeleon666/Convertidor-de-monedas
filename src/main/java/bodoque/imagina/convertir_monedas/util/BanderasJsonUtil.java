package bodoque.imagina.convertir_monedas.util;

import bodoque.imagina.convertir_monedas.modelos.Moneda;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;

public class BanderasJsonUtil {
    private static final String DEFAULT_COLLECTION_FILE = "";
    private final ObjectMapper objectMapper;

    public BanderasJsonUtil() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Carga la colección completa de países desde un archivo JSON.
     * @return Mapa con los datos de los países, usando el código como clave.
     */
    public Map<String, Moneda> loadCountryCollection(Class origenData) {
        Map<String, Moneda> countries = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(origenData.getResource("data/monedas.json").getPath())) {
            JsonNode rootNode = objectMapper.readTree(fis);
            if (rootNode.isArray()) {
                for (JsonNode countryNode : rootNode) {
                    if (countryNode.has("codigo") && countryNode.has("nombre") && countryNode.has("imagenBandera")) {
                        Moneda country = new Moneda();
                        country.setCodigo(countryNode.get("codigo").asText());
                        country.setNombrePais(countryNode.get("nombre").asText());
                        String base64Flag = countryNode.get("imagenBandera").asText();
                        country.setImagenBandera(base64Flag);
                        country.setNombreMoneda(countryNode.get("currencyName").asText());
                        countries.put(country.getCodigo(), country);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando colección de países: " + e.getMessage());
            e.printStackTrace();
        }
        return countries;
    }


    public static Image createImageFromBase64(String base64Flag) {
        // Decodificar la cadena Base64
        byte[] imageBytes = Base64.getDecoder().decode(base64Flag);

        // Crear un InputStream a partir de los bytes decodificados
        InputStream inputStream = new ByteArrayInputStream(imageBytes);

        // Crear la imagen en JavaFX
        return new Image(inputStream);
    }

}
