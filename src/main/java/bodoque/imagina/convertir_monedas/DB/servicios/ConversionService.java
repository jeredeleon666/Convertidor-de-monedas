package bodoque.imagina.convertir_monedas.DB.servicios;

import bodoque.imagina.convertir_monedas.DB.Dao.HistorialConversionDAO;
import bodoque.imagina.convertir_monedas.DB.Dao.MonedaDAO;
import bodoque.imagina.convertir_monedas.DB.Dao.TasaConversionDAO;
import bodoque.imagina.convertir_monedas.modelos.HistorialConversion;
import bodoque.imagina.convertir_monedas.modelos.Moneda;
import bodoque.imagina.convertir_monedas.modelos.TasaConversion;
import bodoque.imagina.convertir_monedas.util.DateTimeUtil;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestionar operaciones de conversión.
 */
public class ConversionService {
    private final HistorialConversionDAO historialDAO;
    private final MonedaDAO monedaDAO;
    private final TasaConversionDAO tasaConversionDAO;

    public ConversionService() {
        this.historialDAO = new HistorialConversionDAO();
        this.monedaDAO = new MonedaDAO();
        this.tasaConversionDAO = new TasaConversionDAO();
    }

    /**
     * Obtener la lista de todas las monedas almacenadas en la base de datos
     * @return Lista de paises con sus banderas y nombre de las monedas de cada pais
     */
    public List<Moneda> listarTodasLasMonedas() {
        return monedaDAO.obtenerTodas();
    }

    /**
     * Obtener una moneda especifica a partir de su codigo
     * @param codigo Identificador del pais de quien se quieren obtener los datos de moneda
     * @return Optional con la moneda si existe
     */
    public Optional<Moneda> obtenerMonedaPorCodigo(String codigo) {
        return monedaDAO.obtenerPorCodigo(codigo);
    }

    /**
     * Guardar en el historial la conversion de una moneda.
     * @param monedaOrigenId  Código de la moneda de origen
     * @param monedaDestinoId Código de la moneda de destino
     * @param montoOrigen     Monto a convertir
     * @param totalConversion Resultado de la conversión
     * @param tasaCambio      Tasa de cambio aplicada
     * @return Optional con el HistorialConversion si fue exitosa
     */
    public Optional<HistorialConversion> registrarConversionEnHistorial(
            String monedaOrigenId,
            String monedaDestinoId,
            double montoOrigen,
            double totalConversion,
            double tasaCambio
    ) {
        HistorialConversion conversion = new HistorialConversion(
                monedaOrigenId,
                monedaDestinoId,
                montoOrigen,
                totalConversion,
                DateTimeUtil.now(),
                tasaCambio
        );

        return historialDAO.guardar(conversion) ? Optional.of(conversion) : Optional.empty();
    }

    /**
     * Obtiene todo el historial de conversiones
     * @return Lista con todas las conversiones registradas
     */
    public List<HistorialConversion> obtenerHistorial() {
        return historialDAO.obtenerTodos();
    }

    /**
     * Obtiene un registro específico del historial por su ID
     * @param idHistorial ID del registro a buscar
     * @return Optional con el registro si existe
     */
    public Optional<HistorialConversion> obtenerHistorialPorId(long idHistorial) {
        return historialDAO.obtenerPorId(idHistorial);
    }

    /**
     * Obtener la tasa de cambio de una moneda por ID
     */
    public TasaConversion obtenerTasaConversion(long idHistorial){
        return tasaConversionDAO.obtenerPorHistorial(idHistorial);
    }
}