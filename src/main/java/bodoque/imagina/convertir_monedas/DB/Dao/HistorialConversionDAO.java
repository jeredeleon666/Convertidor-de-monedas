package bodoque.imagina.convertir_monedas.DB.Dao;

import bodoque.imagina.convertir_monedas.DB.DatabaseManager;
import bodoque.imagina.convertir_monedas.modelos.TasaConversion;
import bodoque.imagina.convertir_monedas.util.DateTimeUtil;
import bodoque.imagina.convertir_monedas.modelos.HistorialConversion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HistorialConversionDAO {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();
    private final TasaConversionDAO tasaConversionDAO = new TasaConversionDAO();

    /**
     * Guarda el registro de conversion de monedas en la base de datos.
     * @param historial Proporcionar el registro para guardar
     * @return verdadero si se guardo correctamente de lo contrario falso.
     */
    public boolean guardar(HistorialConversion historial) {
        String sql = "INSERT INTO HistorialConversion (moneda_origen_id, moneda_destino_id, monto_origen, " +
                "monto_destino, fecha_conversion) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = databaseManager.getConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, historial.getMonedaOrigenId());
            preparedStatement.setString(2, historial.getMonedaDestinoId());
            preparedStatement.setDouble(3, historial.getMontoOrigen());
            preparedStatement.setDouble(4, historial.getMontoDestino());
            preparedStatement.setString(5, DateTimeUtil.localDateTimeToString(historial.getFechaConversion()));

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                    if (resultSet.next()) {
                        long id = resultSet.getLong(1);
                        TasaConversion tasaConversion = new TasaConversion(id, historial.getTasa());
                        tasaConversionDAO.guardar(tasaConversion);
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtener todos los registros del historial.
     * @return Lista de registros de conversión
     */
    public List<HistorialConversion> obtenerTodos() {
        List<HistorialConversion> historial = new ArrayList<>();
        String sql = "SELECT id, moneda_origen_id, moneda_destino_id, monto_origen, monto_destino, fecha_conversion FROM HistorialConversion ORDER BY fecha_conversion DESC;";

        try (Connection connection = databaseManager.getConexion()) {
            if (connection != null) {
                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(sql)) {

                    // First extract all data from ResultSet while it's open
                    List<HistorialConversion> registrosTemporales = new ArrayList<>();
                    while (resultSet.next()) {
                        // Create complete objects while ResultSet is open
                        registrosTemporales.add(crearRegistroDesdeResultSet(resultSet));
                    }

                    // Now that we have extracted all data, we can use forEach safely
                    registrosTemporales.forEach(registro -> historial.add(registro));
                    // Or more simply: historial.addAll(registrosTemporales);
                }
            } else {
                System.err.println("La conexión a la base de datos es nula.");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial: " + e.getMessage());
        }

        return historial;
    }

    /**
     * Recupera un registro de HistorialConversion por su ID.
     * @param idHistorial El ID del historial a buscar.
     * @return Un Optional con el HistorialConversion si existe, o vacío si no.
     */
    public Optional<HistorialConversion> obtenerPorId(Long idHistorial) {
        String sql = """
        SELECT
          id,
          moneda_origen_id,
          moneda_destino_id,
          monto_origen,
          monto_destino,
          fecha_conversion
        FROM HistorialConversion
        WHERE id = ?;
        """;

        try (Connection connection = databaseManager.getConexion();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, idHistorial);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(crearRegistroDesdeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener historial por ID: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Crea un objeto HistorialConversion a partir de un ResultSet
     * @param rs ResultSet con los datos del historial
     * @return Objeto HistorialConversion completo
     * @throws SQLException Si hay un error al acceder a los datos
     */
    private HistorialConversion crearRegistroDesdeResultSet(ResultSet rs) throws SQLException {
        HistorialConversion registro = new HistorialConversion();
        registro.setId(rs.getLong("id"));
        registro.setMonedaOrigenId(rs.getString("moneda_origen_id"));
        registro.setMonedaDestinoId(rs.getString("moneda_destino_id"));
        registro.setMontoOrigen(rs.getDouble("monto_origen"));
        registro.setMontoDestino(rs.getDouble("monto_destino"));
        registro.setFechaConversion(
                DateTimeUtil.stringToLocalDateTime(rs.getString("fecha_conversion"))
        );


        return registro;
    }
}