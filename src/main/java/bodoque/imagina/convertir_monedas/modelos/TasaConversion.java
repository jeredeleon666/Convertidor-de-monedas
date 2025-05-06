package bodoque.imagina.convertir_monedas.modelos;

import java.time.LocalDateTime;

/**
 * Modelo para representar una tasa de conversión vinculada
 * a un registro del historial de conversiones.
 */
public class TasaConversion {
    private long id;
    private long historialConversionId;
    private double tasa;

    public TasaConversion() {}

    public TasaConversion(long historialConversionId, double tasa) {
        this.historialConversionId = historialConversionId;
        this.tasa = tasa;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getHistorialConversionId() {
        return historialConversionId;
    }

    public void setHistorialConversionId(long historialConversionId) {
        this.historialConversionId = historialConversionId;
    }

    public double getTasa() {
        return tasa;
    }

    public void setTasa(double tasa) {
        this.tasa = tasa;
    }
}
