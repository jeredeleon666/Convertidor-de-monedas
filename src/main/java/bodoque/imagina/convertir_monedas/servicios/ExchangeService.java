package bodoque.imagina.convertir_monedas.servicios;


import bodoque.imagina.convertir_monedas.util.ExchangeRateClient;
import java.math.BigDecimal;

public class ExchangeService {
    private static final String API_KEY = "d92657ac96d1e54a8083def9";

    /**
     * Obtiene la tasa y convierte el monto, devolviendo la tasa y el resultado.
     */
    public ConversionResult convert(String fromCode, String toCode, double amount) throws Exception {
        double tasa = ExchangeRateClient
                .getConversionRate(API_KEY, fromCode, toCode);
        double converted = amount * tasa;
        return new ConversionResult(tasa, BigDecimal.valueOf(converted));
    }

    public static class ConversionResult {
        public final double tasa;
        public final BigDecimal resultado;
        public ConversionResult(double tasa, BigDecimal resultado) {
            this.tasa = tasa;
            this.resultado = resultado;
        }
    }
}
