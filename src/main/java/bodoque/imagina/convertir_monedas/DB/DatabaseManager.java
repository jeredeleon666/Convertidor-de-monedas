// src/main/java/bodoque/imagina/convertir_monedas/DB/DatabaseManager.java
package bodoque.imagina.convertir_monedas.DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:conversor_monedas.db";
    private static DatabaseManager instancia;
    private Connection conexion;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver SQLite: " + e.getMessage());
            throw new RuntimeException("No se pudo cargar el driver SQLite.", e);
        }
    }

    /**
     * Crea las tablas si no existen y retorna true si la tabla Moneda está vacía o recién creada.
     */
    public boolean inicarDatabaseYDetectarMonedaVacia() throws SQLException {
        try (Connection conn = getConexion();
             Statement s     = conn.createStatement()) {

            // 1. Tabla Moneda
            s.execute("""
                CREATE TABLE IF NOT EXISTS Moneda (
                  codigo TEXT PRIMARY KEY,
                  nombre_pais TEXT NOT NULL,
                  nombre_moneda TEXT NOT NULL,
                  imagen_bandera TEXT
                );
            """);

            // 2. HistorialConversion
            s.execute("""
                CREATE TABLE IF NOT EXISTS HistorialConversion (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  moneda_origen_id TEXT NOT NULL,
                  moneda_destino_id TEXT NOT NULL,
                  monto_origen REAL NOT NULL,
                  monto_destino REAL NOT NULL,
                  fecha_conversion TEXT NOT NULL,
                  FOREIGN KEY(moneda_origen_id)   REFERENCES Moneda(codigo),
                  FOREIGN KEY(moneda_destino_id)  REFERENCES Moneda(codigo)
                );
            """);

            // 3. TasaConversion
            s.execute("""
                CREATE TABLE IF NOT EXISTS TasaConversion (
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  historial_conversion_id INTEGER NOT NULL,
                  tasa REAL NOT NULL,
                  FOREIGN KEY(historial_conversion_id) REFERENCES HistorialConversion(id)
                );
            """);

            // Índices de rendimiento
            s.execute("CREATE INDEX IF NOT EXISTS idx_hist_origen ON HistorialConversion(moneda_origen_id);");
            s.execute("CREATE INDEX IF NOT EXISTS idx_hist_destino ON HistorialConversion(moneda_destino_id);");
            s.execute("CREATE INDEX IF NOT EXISTS idx_tasa_hist ON TasaConversion(historial_conversion_id);");

            // Comprobar si table Moneda está vacía
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS cnt FROM Moneda;");
            boolean estaVacia = rs.next() && rs.getInt("cnt") == 0;

            System.out.println("Base de datos iniciada. ¿Tabla Moneda vacía? " + estaVacia);
            return estaVacia;
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instancia == null) {
            instancia = new DatabaseManager();
        }
        return instancia;
    }

    public Connection getConexion() throws SQLException {
        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(DB_URL);
            enableForeignKeys(conexion);
        }
        return conexion;
    }

    public void cerrarConexion() {
        if (conexion != null) {
            try {
                conexion.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }

    private void enableForeignKeys(Connection conn) throws SQLException {
        try (Statement st = conn.createStatement()) {
            st.execute("PRAGMA foreign_keys = ON;");
        }
    }
}
