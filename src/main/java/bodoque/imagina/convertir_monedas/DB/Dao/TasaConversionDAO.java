package bodoque.imagina.convertir_monedas.DB.Dao;

import bodoque.imagina.convertir_monedas.DB.DatabaseManager;
import bodoque.imagina.convertir_monedas.modelos.TasaConversion;

import java.sql.*;

/**
 * DAO para la tabla TasaConversion.
 */
public class TasaConversionDAO {
    private final DatabaseManager db = DatabaseManager.getInstance();

    /**
     * Inserta una nueva tasa vinculada a un historial de conversión.
     * @param tasaConv Objeto con historialConversionId y valor de la tasa.
     * @return el ID generado de la nueva fila, o -1 en caso de error.
     */
    public long guardar(TasaConversion tasaConv) {
        String sql = """
            INSERT INTO TasaConversion (
              historial_conversion_id,
              tasa
            ) VALUES (?, ?);
            """;

        try (Connection conn = db.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, tasaConv.getHistorialConversionId());
            ps.setDouble(2, tasaConv.getTasa());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("No se insertó la tasa de conversión.");
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    long id = keys.getLong(1);
                    tasaConv.setId(id);
                    return id;
                } else {
                    throw new SQLException("No se devolvió ID generado.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar TasaConversion: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Obtiene la tasa asociada a un historial de conversión dado.
     * @param historialId ID de HistorialConversion
     * @return TasaConversion o null si no existe
     */
    public TasaConversion obtenerPorHistorial(long historialId) {
        String sql = """
            SELECT id, historial_conversion_id, tasa
              FROM TasaConversion
             WHERE historial_conversion_id = ?;
            """;

        try (Connection conn = db.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, historialId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TasaConversion tc = new TasaConversion();
                    tc.setId(rs.getLong("id"));
                    tc.setHistorialConversionId(rs.getLong("historial_conversion_id"));
                    tc.setTasa(rs.getDouble("tasa"));
                    return tc;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener TasaConversion: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza el valor de la tasa para un historial de conversión.
     * @param tasaConv Objeto con ID y nuevo valor de tasa.
     * @return true si se actualizó al menos una fila.
     */
    public boolean actualizar(TasaConversion tasaConv) {
        String sql = """
            UPDATE TasaConversion
               SET tasa = ?
             WHERE id = ?;
            """;

        try (Connection conn = db.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, tasaConv.getTasa());
            ps.setLong(2, tasaConv.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar TasaConversion: " + e.getMessage());
            return false;
        }
    }

}
