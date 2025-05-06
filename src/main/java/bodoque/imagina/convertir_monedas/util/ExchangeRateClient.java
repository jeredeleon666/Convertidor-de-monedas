package bodoque.imagina.convertir_monedas.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExchangeRateClient {
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6";
    private static final ObjectMapper mapper = new ObjectMapper();

    /**
     * Obtiene la tasa de conversión directa de fromCurrency a toCurrency
     * usando la ruta "latest".
     */
    public static double getConversionRate(String apiKey, String fromCurrency, String toCurrency) throws Exception {
        String endpoint = String.format("%s/%s/latest/%s", BASE_URL, apiKey, fromCurrency);
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Error HTTP: " + conn.getResponseCode());
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
            // Leer toda la respuesta JSON
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) json.append(line);

            // Mapear a un contenedor genérico
            @SuppressWarnings("unchecked")
            var tree = mapper.readTree(json.toString()).get("conversion_rates");
            if (tree == null || tree.get(toCurrency) == null) {
                throw new RuntimeException("Moneda destino no encontrada: " + toCurrency);
            }
            return tree.get(toCurrency).asDouble();
        } finally {
            conn.disconnect();
        }
    }

    /**
     * Convierte un monto de fromCurrency a toCurrency, redondeando a 4 decimales.
     */
    public BigDecimal convert(
            String apiKey,
            String fromCurrency,
            String toCurrency,
            BigDecimal amount
    ) throws Exception {
        double rate = getConversionRate(apiKey, fromCurrency, toCurrency);
        return amount
                .multiply(BigDecimal.valueOf(rate))
                .setScale(4, RoundingMode.HALF_UP);
    }
}
