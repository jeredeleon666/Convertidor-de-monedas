package bodoque.imagina.convertir_monedas.modelos;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Moneda {
    @JsonProperty("codigo")
    private String codigo;

    @JsonProperty("nombre")
    private String nombrePais;

    @JsonProperty("currencyName")
    private String nombreMoneda;

    @JsonProperty("imagenBandera")
    private String imagenBandera;

    // Constructor necesario para deserialización
    public Moneda() {}

    // Constructor completo
    public Moneda(String codigo, String nombrePais, String nombreMoneda, String imagenBandera) {
        this.codigo = codigo;
        this.nombrePais = nombrePais;
        this.nombreMoneda = nombreMoneda;
        this.imagenBandera = imagenBandera;
    }

    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombrePais() {
        return nombrePais;
    }

    public void setNombrePais(String nombrePais) {
        this.nombrePais = nombrePais;
    }

    public String getNombreMoneda() {
        return nombreMoneda;
    }

    public void setNombreMoneda(String nombreMoneda) {
        this.nombreMoneda = nombreMoneda;
    }

    public String getImagenBandera() {
        return imagenBandera;
    }

    public void setImagenBandera(String imagenBandera) {
        this.imagenBandera = imagenBandera;
    }

    @Override
    public String toString() {
        return "Moneda{" +
                "codigo='" + codigo + '\'' +
                ", nombrePais='" + nombrePais + '\'' +
                ", nombreMoneda='" + nombreMoneda + '\'' +
                ", imagenBandera='" + imagenBandera + '\'' +
                '}';
    }
}