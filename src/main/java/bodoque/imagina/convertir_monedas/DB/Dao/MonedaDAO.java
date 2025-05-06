package bodoque.imagina.convertir_monedas.DB.Dao;

import bodoque.imagina.convertir_monedas.DB.DatabaseManager;
import bodoque.imagina.convertir_monedas.modelos.Moneda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MonedaDAO {
    private final DatabaseManager databaseManager = DatabaseManager.getInstance();

    public  MonedaDAO(){
        ;
    }

    /**
     * Guardar una moneda nueva en la base de datos.
     * @param moneda La nueva moneda a guardar
     * @return verdadero si se guardo correctamente de lo contrario falso.
     */
    public boolean guardar(Moneda moneda){
        String sql = "INSERT INTO Moneda (codigo, nombre_pais, nombre_moneda, imagen_bandera) VALUES (?, ?, ?, ?)";

        try (Connection connection = databaseManager.getConexion();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setString(1, moneda.getCodigo());
            preparedStatement.setString(2, moneda.getNombrePais());
            preparedStatement.setString(3, moneda.getNombreMoneda());
            preparedStatement.setString(4,moneda.getImagenBandera());

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar el nuevo registro de moneda: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina una moneda de la base de datos.
     * @param codigo El código de la moneda a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminar(String codigo) {
        String sql = "DELETE FROM Moneda WHERE codigo = ?";

        try (Connection connection = databaseManager.getConexion();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, codigo);

            return preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar moneda: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene una moneda por su código.
     * @param codigo El código de la moneda
     * @return Optional con la moneda si existe, Optional vacío en caso contrario
     */
    public Optional<Moneda> obtenerPorCodigo(String codigo) {
        String sql = "SELECT codigo, nombre_pais, nombre_moneda, imagen_bandera FROM Moneda WHERE codigo = ?";

        try (Connection connection = databaseManager.getConexion();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            pstmt.setString(1, codigo);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Moneda moneda = new Moneda();
                    moneda.setCodigo(rs.getString("codigo"));
                    moneda.setNombrePais(rs.getString("nombre_pais"));
                    moneda.setNombreMoneda(rs.getString("nombre_moneda"));
                    moneda.setImagenBandera(rs.getString("imagen_bandera"));

                    return Optional.of(moneda);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener informacion de moneda: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * Obtiene todas las monedas almacenadas en la base de datos.
     * @return Lista de monedas
     */
    public List<Moneda> obtenerTodas() {
        List<Moneda> monedas = new ArrayList<>();
        String sql = "SELECT codigo, nombre_pais, nombre_moneda, imagen_bandera FROM Moneda ORDER BY nombre_moneda";

        try (Connection connection = databaseManager.getConexion();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                Moneda moneda = new Moneda();
                moneda.setCodigo(resultSet.getString("codigo"));
                moneda.setNombrePais(resultSet.getString("nombre_pais"));
                moneda.setNombreMoneda(resultSet.getString("nombre_moneda"));
                moneda.setImagenBandera(resultSet.getString("imagen_bandera"));

                monedas.add(moneda);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener monedas: " + e.getMessage());
        }

        return monedas;
    }
}
