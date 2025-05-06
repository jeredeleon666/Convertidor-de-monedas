package bodoque.imagina.convertir_monedas;

import bodoque.imagina.convertir_monedas.DB.DatabaseManager;
import bodoque.imagina.convertir_monedas.DB.servicios.ConversionService;
import bodoque.imagina.convertir_monedas.modelos.HistorialConversion;
import bodoque.imagina.convertir_monedas.modelos.Moneda;
import bodoque.imagina.convertir_monedas.modelos.TasaConversion;
import bodoque.imagina.convertir_monedas.util.Controllers.ItemController;
import bodoque.imagina.convertir_monedas.util.Controllers.HistorialItemController;
import bodoque.imagina.convertir_monedas.util.DateTimeUtil;
import bodoque.imagina.convertir_monedas.util.ExchangeRateClient;
import bodoque.imagina.convertir_monedas.util.BanderasJsonUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;

import java.util.prefs.Preferences;
import java.util.regex.Pattern;

/**
 * Controlador principal de la aplicación de conversión de monedas.
 * Gestiona la interfaz de usuario y la lógica de conversión de monedas.
 */
public class ConversorMonedas implements Initializable {

    // FXML elementos del panel de historial
    @FXML private ListView<HistorialConversion> historialListView;

    // FXML elementos de la barra de búsqueda
    @FXML private TextField searchField;

    // FXML elementos de moneda origen
    @FXML private ComboBox<Moneda> comboSource;
    @FXML private TextField fieldAmount;

    // FXML elementos de moneda destino
    @FXML private ComboBox<Moneda> comboTarget;

    // FXML elementos de resultados
    @FXML private Label labelSourceAmount;
    @FXML private Label labelSourceCurrency;
    @FXML private Label labelTargetAmount;
    @FXML private Label labelTargetCurrency;
    @FXML private Label labelExchangeRate;
    @FXML private Label labelLastUpdate;

    // FXML elementos del gráfico
    @FXML private LineChart<Number, Number> exchangeRateChart;
    @FXML private NumberAxis xAxis;
    @FXML private NumberAxis yAxis;

    // FXML contenedores para resposividad
    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    @FXML private HBox titleBar;

    // FXML botones
    @FXML private Button btnConvert;
    @FXML private Button btnMinimazar;
    @FXML private Button btnCerrar;
    @FXML private ToggleButton themeToggle;

    // Servicios y utilidades
    private final static ConversionService conversionService = new ConversionService();

    // Patrones para normalizar texto
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    // Cache para normalización de texto
    private final Map<String, String> normalizationCache = new WeakHashMap<>();

    // Variables para datos y filtros
    private Map<String, Moneda> monedasMap;
    private ObservableList<Moneda> allMonedas;
    private ObservableList<HistorialConversion> historialConversiones;
    private WeakHashMap<String, XYChart.Series<Number, Number>> exchangeRateSeriesMap;

    // Variables para arrastrar la ventana
    private double xOffset = 0;
    private double yOffset = 0;

    // Preferencias para guardar configuración
    private Preferences prefs;
    private static final String THEME_PREF_KEY = "theme_dark_mode";

    // Función para obtener moneda por código (cache)
    private final Function<String, Moneda> obtenerMonedaPorCodigo = codigo -> monedasMap.get(codigo);

    /**
     * Inicializa el controlador. Este método es llamado automáticamente por JavaFX
     * después de que el archivo FXML ha sido cargado.
     *
     * @param location URL del archivo FXML
     * @param resources ResourceBundle que contiene los recursos localizados
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Inicializar preferencias
            prefs = Preferences.userNodeForPackage(ConversorMonedas.class);

            // Inicializar componentes en orden lógico
            inicializarBaseDeDatos();
            initializeData();
            initializeUI();
            cargarHistorialDeConversiones();
        } catch (Exception e) {
            mostrarAlerta("Error de inicialización", "No se pudo inicializar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicializa todos los datos necesarios para la aplicación
     */
    private void initializeData() {
        // Inicializar mapa de monedas y lista observable
        List<Moneda> monedas = conversionService.listarTodasLasMonedas();
        monedasMap = new HashMap<>(monedas.size());
        for (Moneda moneda : monedas) {
            monedasMap.put(moneda.getCodigo(), moneda);
        }

        allMonedas = FXCollections.observableArrayList(monedas);

        historialConversiones = FXCollections.observableArrayList();
        exchangeRateSeriesMap = new WeakHashMap<>();
    }

    /**
     * Inicializa todos los componentes de la interfaz de usuario
     */
    private void initializeUI() {
        initializeResponsive();
        initializeThemeToggle();
        initializeSearch();

        // Configurar los componentes principales
        setupWindowButtons();
        setupWindowDragging();
        setupComboBox(comboSource);
        setupComboBox(comboTarget);
        setupHistorialListView();
        setupConvertButton();
    }

    /**
     * Obtiene el Stage de la aplicación
     * @return El Stage principal de la aplicación
     */
    private Stage getStage() {
        return (Stage) titleBar.getScene().getWindow();
    }

    /**
     * Configura los botones de minimizar y cerrar de la ventana
     */
    private void setupWindowButtons() {
        btnMinimazar.setOnAction(evt -> {
            Stage stage = getStage();
            stage.setIconified(true);
        });
        btnCerrar.setOnAction(evt -> {
            Stage stage = getStage();
            stage.close();
        });
    }

    /**
     * Permite arrastrar la ventana al arrastrar la barra de título
     */
    private void setupWindowDragging() {
        // Captura posición inicial
        titleBar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        // Mueve ventana
        titleBar.setOnMouseDragged(event -> {
            Stage stage = getStage();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    /**
     * Inicializa la base de datos creando las tablas necesarias si no existen.
     */
    private static void inicializarBaseDeDatos() {
        System.out.println("Inicializando la base de datos...");
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            boolean dbVacia = dbManager.inicarDatabaseYDetectarMonedaVacia();
            if (dbVacia){
                MigrarDatosJsonADB.initializeDatabaseAndMigrate();
            }
            System.out.println("Base de datos inicializada correctamente.");
        } catch (Exception e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
            throw new RuntimeException("Error al inicializar la base de datos", e);
        }
    }

    /**
     * Configura la interfaz para ser responsive
     */
    private void initializeResponsive() {
        // Configurar para que la interfaz sea responsive
        if (scrollPane != null) {
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
        }

        // Hacer que los componentes ocupen todo el espacio disponible
        if (mainContainer != null) {
            VBox.setVgrow(historialListView, Priority.ALWAYS);
            VBox.setVgrow(exchangeRateChart, Priority.ALWAYS);
        }
    }

    /**
     * Configura un ComboBox para selección de monedas con funcionalidad de búsqueda
     *
     * @param comboBox El ComboBox a configurar
     */
    private void setupComboBox(ComboBox<Moneda> comboBox) {
        // Configurar el ComboBox como búsqueda editable
        comboBox.setEditable(true);
        FilteredList<Moneda> filteredItems = new FilteredList<>(allMonedas, m -> true);
        comboBox.setItems(filteredItems);

        // Configurar el conversor para mostrar/parsear el nombre de la moneda
        comboBox.setConverter(createMonedaStringConverter());

        // Configurar la fábrica de celdas para el ComboBox
        Callback<ListView<Moneda>, ListCell<Moneda>> cellFactory = createMonedaCellFactory();
        comboBox.setCellFactory(cellFactory);
        comboBox.setButtonCell(cellFactory.call(null));

        // Configurar los manejadores de eventos
        setupComboBoxEventHandlers(comboBox, filteredItems, cellFactory);
    }

    /**
     * Crea un conversor de String para los ComboBox de monedas
     *
     * @return Un StringConverter personalizado para objetos Moneda
     */
    private StringConverter<Moneda> createMonedaStringConverter() {
        return new StringConverter<Moneda>() {
            @Override
            public String toString(Moneda moneda) {
                return moneda != null ? moneda.getNombrePais() : "";
            }

            @Override
            public Moneda fromString(String text) {
                if (text == null || text.isEmpty()) return null;

                String normalized = normalizeText(text);
                return monedasMap.values().stream()
                        .filter(m -> normalizeText(m.getNombrePais()).contains(normalized))
                        .findFirst()
                        .orElse(null);
            }
        };
    }

    /**
     * Crea una fábrica de celdas personalizada para mostrar monedas con banderas
     *
     * @return Una fábrica de celdas para ListCells de Moneda
     */
    private Callback<ListView<Moneda>, ListCell<Moneda>> createMonedaCellFactory() {
        return listView -> new ListCell<>() {
            @Override
            protected void updateItem(Moneda item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("itemLista.fxml"));
                    HBox hBox = loader.load();
                    ItemController controller = loader.getController();

                    Image band = BanderasJsonUtil.createImageFromBase64(item.getImagenBandera());
                    controller.setItem(band, item.getNombrePais());

                    hBox.setMaxHeight(20);
                    setGraphic(hBox);
                    setText(null);
                } catch (IOException e) {
                    setGraphic(null);
                    setText(item.getNombrePais());
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * Configura los manejadores de eventos para los ComboBox de monedas
     *
     * @param comboBox El ComboBox a configurar
     * @param filteredItems Lista filtrada de monedas para el ComboBox
     * @param cellFactory Fábrica de celdas del ComboBox
     */
    private void setupComboBoxEventHandlers(ComboBox<Moneda> comboBox,
                                            FilteredList<Moneda> filteredItems,
                                            Callback<ListView<Moneda>, ListCell<Moneda>> cellFactory) {
        // Evento de clic para mostrar la lista desplegable
        comboBox.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if (!comboBox.isEditable()) {
                comboBox.setEditable(true);
                comboBox.getEditor().clear();
                comboBox.show();
                event.consume();
            }
        });

        // Filtrado en tiempo real según texto ingresado
        comboBox.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final String searchText = normalizeText(newVal == null ? "" : newVal);

            filteredItems.setPredicate(moneda ->
                    searchText.isEmpty() || normalizeText(moneda.getNombrePais()).contains(searchText)
            );

            if (!comboBox.isShowing() && !searchText.isEmpty()) {
                comboBox.show();
            }
        });

        // Estado no editable al ocultar lista
        comboBox.setOnHidden(e -> {
            comboBox.setEditable(false);
            comboBox.setButtonCell(cellFactory.call(null));
        });
    }

    /**
     * Configura el ListView para mostrar el historial de conversiones
     */
    private void setupHistorialListView() {
        historialListView.setItems(historialConversiones);

        historialListView.setCellFactory(param -> new ListCell<HistorialConversion>() {
            @Override
            protected void updateItem(HistorialConversion item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("itemHistorial.fxml"));
                    HBox hBox = loader.load();
                    HistorialItemController controller = loader.getController();

                    // Obtener monedas mediante la función de caché
                    Moneda paisOrigen = obtenerMonedaPorCodigo.apply(item.getMonedaOrigenId());
                    Moneda paisDestino = obtenerMonedaPorCodigo.apply(item.getMonedaDestinoId());
                    String cadena = "1 " + paisOrigen.getCodigo() + " = " + item.getTasa() + " " + paisDestino.getCodigo();
                    if (paisOrigen != null && paisDestino != null) {
                        controller.setItem(
                                DateTimeUtil.localDateTimeToString(item.getFechaConversion()),
                                paisOrigen.getNombrePais(),
                                paisDestino.getNombrePais(),
                                item.getMontoOrigen(),
                                item.getMontoDestino(),
                                BanderasJsonUtil.createImageFromBase64(paisOrigen.getImagenBandera()),
                                BanderasJsonUtil.createImageFromBase64(paisDestino.getImagenBandera()),
                                cadena
                        );
                    }

                    setGraphic(hBox);
                } catch (IOException e) {
                    e.printStackTrace();
                    setText(item.toString());
                }
            }
        });
    }

    /**
     * Configura el botón de conversión de monedas
     */
    private void setupConvertButton() {
        btnConvert.setOnAction(event -> {
            try {
                realizarConversion();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error inesperado", "Ha ocurrido un error al realizar la conversión: " + e.getMessage());
            }
        });
    }

    /**
     * Realiza la conversión de monedas según los datos ingresados por el usuario
     */
    private void realizarConversion() {
        // Obtener los datos de conversión
        Moneda monedaOrigen = comboSource.getValue();
        Moneda monedaDestino = comboTarget.getValue();

        if (monedaOrigen == null || monedaDestino == null) {
            mostrarAlerta("Error de conversión", "Debe seleccionar ambas monedas para realizar la conversión.");
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(fieldAmount.getText().replace(',', '.'));
        } catch (NumberFormatException e) {
            mostrarAlerta("Error de conversión", "El monto ingresado no es válido.");
            return;
        }

        // Determinar moneda base para la API
        String monedaBase = getMonedaBaseParaAPI(monedaOrigen.getCodigo());

        try {
            // Obtener tasa de cambio y calcular conversión
            double tasaCambio = ExchangeRateClient.getConversionRate(
                    "d92657ac96d1e54a8083def9",
                    monedaBase,
                    monedaDestino.getCodigo()
            );

            double montoConvertido = monto * tasaCambio;

            // Crear objeto de conversión para historial
            HistorialConversion conversion = new HistorialConversion(
                    monedaOrigen.getCodigo(),
                    monedaDestino.getCodigo(),
                    monto,
                    montoConvertido,
                    DateTimeUtil.now(),
                    tasaCambio
            );

            // Registrar la conversión en la base de datos
            conversionService.registrarConversionEnHistorial(
                    monedaOrigen.getCodigo(),
                    monedaDestino.getCodigo(),
                    monto,
                    montoConvertido,
                    tasaCambio
            );

            // Actualizar la interfaz
            actualizarInterfazTrasConversion(conversion);

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la conversión", e);
        }
    }

    /**
     * Determina la moneda base adecuada para la API de conversión
     *
     * @param codigoMoneda Código de la moneda a verificar
     * @return Código de moneda a usar como base para la API
     */
    private String getMonedaBaseParaAPI(String codigoMoneda) {
        // Determinar qué moneda usar para la API
        return codigoMoneda.length() > 2 ? codigoMoneda : "EUR";
    }

    /**
     * Actualiza la interfaz tras realizar una conversión exitosa
     *
     * @param conversion Objeto con los datos de la conversión realizada
     */
    private void actualizarInterfazTrasConversion(HistorialConversion conversion) {
        // Formatear valores numéricos
        DecimalFormat df = new DecimalFormat("#,##0.00");

        // Obtener monedas
        Moneda monedaOrigen = obtenerMonedaPorCodigo.apply(conversion.getMonedaOrigenId());
        Moneda monedaDestino = obtenerMonedaPorCodigo.apply(conversion.getMonedaDestinoId());

        // Actualizar etiquetas de montos
        labelSourceAmount.setText(df.format(conversion.getMontoOrigen()));
        labelTargetAmount.setText(df.format(conversion.getMontoDestino()));

        // Actualizar etiquetas de monedas
        labelSourceCurrency.setText(monedaOrigen.getCodigo());
        labelTargetCurrency.setText(monedaDestino.getCodigo());

        // Actualizar etiqueta de tasa de cambio
        labelExchangeRate.setText(String.format("1 %s = %s %s",
                monedaOrigen.getCodigo(),
                df.format(conversion.getTasa()),
                monedaDestino.getCodigo()));

        // Actualizar etiqueta de última actualización
        if (labelLastUpdate != null) {
            labelLastUpdate.setText(DateTimeUtil.localDateTimeToString(conversion.getFechaConversion()));
        }

        // Agregar al inicio del historial
        historialConversiones.add(0, conversion);
    }

    /**
     * Inicializa la funcionalidad de búsqueda global en la aplicación
     */
    private void initializeSearch() {
        if (searchField == null) return;

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            String searchText = normalizeText(newValue);

            // Aplicar filtro a ambos ComboBox
            if (comboSource.getItems() instanceof FilteredList) {
                FilteredList<Moneda> filteredItems = (FilteredList<Moneda>) comboSource.getItems();
                filteredItems.setPredicate(moneda ->
                        searchText.isEmpty() || normalizeText(moneda.getNombrePais()).contains(searchText)
                );
            }

            if (comboTarget.getItems() instanceof FilteredList) {
                FilteredList<Moneda> filteredItems = (FilteredList<Moneda>) comboTarget.getItems();
                filteredItems.setPredicate(moneda ->
                        searchText.isEmpty() || normalizeText(moneda.getNombrePais()).contains(searchText)
                );
            }
        });
    }

    /**
     * Inicializa el botón de cambio de tema y configura la persistencia
     * del estado del tema en las preferencias del usuario.
     */
    private void initializeThemeToggle() {
        if (themeToggle == null) return;

        Image lightModeIcon = new Image(getClass().getResourceAsStream("dia.png"));
        Image darkModeIcon  = new Image(getClass().getResourceAsStream("noche.png"));

        Function<Boolean, ImageView> createIcon = isDarkMode -> {
            ImageView icon = new ImageView(isDarkMode ? darkModeIcon : lightModeIcon);
            icon.setFitHeight(20);
            icon.setFitWidth(20);
            icon.setPreserveRatio(true);
            return icon;
        };

        // Recuperar preferencia previa
        boolean isDarkMode = prefs.getBoolean(THEME_PREF_KEY, false);
        themeToggle.setSelected(isDarkMode);
        themeToggle.setGraphic(createIcon.apply(isDarkMode));

        // Escuchar cuándo el toggle ya está en una Scene válida
        themeToggle.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                // Aplicar clase inicial
                newScene.getRoot().getStyleClass().removeAll("light", "dark");
                newScene.getRoot().getStyleClass().add(isDarkMode ? "dark" : "light");

                // Y ahora convertir el listener de cambio de estado
                themeToggle.selectedProperty().addListener((obs, oldVal, newVal) -> {
                    themeToggle.setGraphic(createIcon.apply(newVal));
                    newScene.getRoot().getStyleClass().removeAll("light", "dark");
                    newScene.getRoot().getStyleClass().add(newVal ? "dark" : "light");
                    prefs.putBoolean(THEME_PREF_KEY, newVal);
                });
            }
        });
    }


    /**
     * Carga el historial de conversiones desde la base de datos
     */
    private void cargarHistorialDeConversiones() {
        try {
            List<HistorialConversion> conversiones = conversionService.obtenerHistorial();
            conversiones.forEach(hist -> {
                TasaConversion tasa = conversionService.obtenerTasaConversion(hist.getId());
                hist.setTasa(tasa.getTasa());
            });
            // Limpiar lista actual
            historialConversiones.clear();

            if (conversiones.isEmpty()) {
                System.out.println("Historial de conversión vacío.");
                return;
            }

            // Agregar conversiones y ordenar por fecha
            historialConversiones.addAll(conversiones);
            FXCollections.sort(historialConversiones,
                    (c1, c2) -> c2.getFechaConversion().compareTo(c1.getFechaConversion()));

            System.out.println("Historial cargado: " + conversiones.size() + " conversiones");

        } catch (Exception e) {
            System.err.println("Error al cargar el historial de conversiones: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Normaliza un texto para realizar búsquedas insensibles a acentos y mayúsculas
     * Utiliza una caché para mejorar el rendimiento.
     *
     * @param text Texto a normalizar
     * @return Texto normalizado en minúsculas sin acentos ni espacios
     */
    private String normalizeText(String text) {
        if (text == null) return "";

        return normalizationCache.computeIfAbsent(text, k -> {
            String normalized = Normalizer.normalize(k, Normalizer.Form.NFD);
            normalized = NONLATIN.matcher(normalized).replaceAll("");
            normalized = WHITESPACE.matcher(normalized).replaceAll("");
            return normalized.toLowerCase();
        });
    }

    /**
     * Muestra una alerta de error
     *
     * @param titulo Título de la alerta
     * @param mensaje Mensaje a mostrar en la alerta
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}