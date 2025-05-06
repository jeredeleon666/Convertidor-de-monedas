package bodoque.imagina.convertir_monedas.DB.servicios;

import bodoque.imagina.convertir_monedas.DB.Dao.MonedaDAO;
import bodoque.imagina.convertir_monedas.modelos.Moneda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MonedaService {
    private final MonedaDAO monedaDAO = new MonedaDAO();

    public MonedaService(){
        ;
    }

    /**
     * Obtener todas las monedas disponibles.
     * @return Lista de monedas
     */
    public Map<String,Moneda> obtenerTodasLasMonedas(){
        Map<String,Moneda> paises = new HashMap<>();

        for (Moneda elemento : monedaDAO.obtenerTodas()) {
            paises.put(elemento.getCodigo(),elemento);
        }

        return paises;
    }

    /**
     * Obtener una moneda basado en su codigo
     * @param codigo Codigo de pais de la moneda
     * @return Optional  Un valor nulo si no se encuentra y el valor correcto si sucede lo contrario.
     */
    public Optional<Moneda> buscarPorCodigo(String codigo) {
        return monedaDAO.obtenerPorCodigo(codigo);
    }

    /**
     * Guardar una nueva moneda
     * @param moneda  Datos de la nueva moneda
     * @return verdadero si se guarda correctamente de lo contrario falso
     */
    public boolean guardarMoneda(Moneda moneda){
        return monedaDAO.guardar(moneda);
    }

    /**
     * Elimina una moneda por su código.
     * @param codigo Código de la moneda a eliminar
     * @return true si se eliminó correctamente
     */
    public boolean eliminarMoneda(String codigo) {
        return monedaDAO.eliminar(codigo);
    }
}
