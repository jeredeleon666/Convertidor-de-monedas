package bodoque.imagina.convertir_monedas.modelos;

import java.time.LocalDateTime;

/**
 * Clase que representa el historial de conversiones de monedas
 * con información detallada de tasas y fuentes de datos
 */
public class HistorialConversion {
    // Campos base
    private Long id;
    private String monedaOrigenId;
    private String monedaDestinoId;
    private Double montoOrigen;
    private Double montoDestino;
    private Double tasa;
    private LocalDateTime fechaConversion;

    // Constructor por defecto
    public HistorialConversion() {
        // Constructor vacío
    }

    /**
     * Constructor principal con información básica de la conversión
     */
    public HistorialConversion(String monedaOrigenId, String monedaDestinoId,
                               Double montoOrigen, Double montoDestino,
                               LocalDateTime fechaConversion, Double tasa) {
        this.monedaOrigenId = monedaOrigenId;
        this.monedaDestinoId = monedaDestinoId;
        this.montoOrigen = montoOrigen;
        this.montoDestino = montoDestino;
        this.fechaConversion = fechaConversion;
        this.tasa = tasa;
    }




    // Getters y setters originales
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMonedaOrigenId() {
        return monedaOrigenId;
    }

    public void setMonedaOrigenId(String monedaOrigenId) {
        this.monedaOrigenId = monedaOrigenId;
    }

    public String getMonedaDestinoId() {
        return monedaDestinoId;
    }

    public void setMonedaDestinoId(String monedaDestinoId) {
        this.monedaDestinoId = monedaDestinoId;
    }

    public Double getMontoOrigen() {
        return montoOrigen;
    }

    public void setMontoOrigen(Double montoOrigen) {
        this.montoOrigen = montoOrigen;
    }

    public Double getMontoDestino() {
        return montoDestino;
    }

    public void setMontoDestino(Double montoDestino) {
        this.montoDestino = montoDestino;
    }

    public Double getTasa() {
        return tasa;
    }

    public void setTasa(Double tasa) {
        this.tasa = tasa;
    }

    public LocalDateTime getFechaConversion() {
        return fechaConversion;
    }

    public void setFechaConversion(LocalDateTime fechaConversion) {
        this.fechaConversion = fechaConversion;
    }



    @Override
    public String toString() {
        return String.format(
                "%s %.2f → %s %.2f (Tasa: %.4f, Fuente: %s, %s)",
                monedaOrigenId, montoOrigen, monedaDestinoId, montoDestino,
                tasa
        );
    }
}