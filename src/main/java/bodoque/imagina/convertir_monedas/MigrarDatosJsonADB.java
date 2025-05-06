// src/main/java/bodoque/imagina/convertir_monedas/MigrarDatosJsonADB.java
package bodoque.imagina.convertir_monedas;

import bodoque.imagina.convertir_monedas.DB.DatabaseManager;
import bodoque.imagina.convertir_monedas.DB.servicios.MonedaService;
import bodoque.imagina.convertir_monedas.modelos.Moneda;
import bodoque.imagina.convertir_monedas.util.BanderasJsonUtil;

import java.util.Map;

/**
 * Utilidad para inicializar la base de datos y, si la tabla Moneda está vacía,
 * migrar automáticamente los datos desde el JSON.
 */
public class MigrarDatosJsonADB {

    /**
     * Inicializa la base de datos (crea tablas si es necesario) y, si la tabla
     * Moneda está vacía, realiza la migración desde el JSON.
     *
     * @return El número de monedas migradas (0 si no se migró nada).
     * @throws Exception Si ocurre cualquier error durante la inicialización o migración.
     */
    public static int initializeDatabaseAndMigrate() throws Exception {
        DatabaseManager dbManager = DatabaseManager.getInstance();
        boolean tablaMonedaVacia = dbManager.inicarDatabaseYDetectarMonedaVacia();

        int migradas = 0;
        if (tablaMonedaVacia) {
            Map<String, Moneda> monedasJson = cargarDatosDesdeJson();
            migradas = migrarDatosABaseDeDatos(monedasJson);
        }

        // Opcional: cierra la conexión aquí, o deja que quien llame la gestione
        dbManager.cerrarConexion();
        return migradas;
    }

    private static Map<String, Moneda> cargarDatosDesdeJson() {
        BanderasJsonUtil loader = new BanderasJsonUtil();
        return loader.loadCountryCollection(MigrarDatosJsonADB.class);
    }

    private static int migrarDatosABaseDeDatos(Map<String, Moneda> monedasJson) {
        MonedaService monedaService = new MonedaService();
        int contador = 0;

        for (Moneda modelo : monedasJson.values()) {
            Moneda m = new Moneda();
            m.setCodigo(modelo.getCodigo());
            m.setNombrePais(modelo.getNombrePais());
            m.setNombreMoneda(modelo.getNombreMoneda());
            m.setImagenBandera(modelo.getImagenBandera());

            if (monedaService.guardarMoneda(m)) {
                contador++;
            }
        }

        return contador;
    }
}
