# Convertidor de Monedas con Historial Integrado 💱

Aplicación de escritorio para convertir divisas en tiempo real, con historial persistente y gestión local.
<img width="1201" alt="Captura de pantalla 2025-05-05 a la(s) 23 26 54" src="https://github.com/user-attachments/assets/872b9dc9-18da-4886-931f-0a5ecf632894" />


## Características

- Conversión en tiempo real usando ExchangeRate-API
- Historial completo de conversiones con timestamp
- Base de datos SQLite integrada
- Gráficos interactivos de evolución de tasas
- Temas claro/oscuro personalizables
- Búsqueda inteligente y catálogo de monedas

## Tecnologías

- JavaFX 24
- SQLite
- ExchangeRate-API
- Gradle
- JSON
- CSS moderno

## Requisitos

- Java JDK 23+
- JavaFX SDK 24
- 500 MB de espacio libre
- Conexión a Internet para tasas en tiempo real

## Modelo de Base de Datos

El modelo se compone de tres tablas principales:

- **Moneda**: Almacena información sobre las divisas disponibles
- **HistorialConversion**: Registra cada conversión realizada
- **TasaConversion**: Guarda las tasas de cada conversión para análisis históricos

## Instalación y Uso

1. Clonar repositorio:
   ```bash
   git clone https://github.com/jeredeleon666/Convertidor-de-monedas.git
   ```

2. Configurar API key y los parametros de ejecucion:
   
   TU_API_KEY
<img width="799" alt="Captura de pantalla 2025-05-05 a la(s) 23 41 55" src="https://github.com/user-attachments/assets/1af6c2bc-1ae5-42c0-9796-da8e8e4e042f" />

   

4. Añadir JavaFX al build (en `build.gradle`):
   ```groovy
   dependencies {
       implementation "org.openjfx:javafx-controls:24"
       implementation "org.openjfx:javafx-fxml:24"
   }

   javafx {
       version = "24"
       modules = ["javafx.controls", "javafx.fxml"]
   }

   />

   ```

5. Ejecutar con Gradle:
   ```bash
   ./gradlew run
   ```

### Configuración de parámetros JVM

Para ejecutar JavaFX en cualquier plataforma, configura los siguientes parámetros JVM:

```bash
--module-path /ruta/a/javafx-sdk-24/lib --add-modules javafx.controls,javafx.fxml
```

- **IntelliJ IDEA**: Run → Edit Configurations → VM options
- **Eclipse**: Run Configurations → Arguments → VM arguments
- **Línea de comandos**:
  ```bash
  java --module-path /ruta/a/javafx-sdk-24/lib --add-modules javafx.controls,javafx.fxml -jar convertidor-monedas.jar
  ```

## Contribución

1. Haz fork del proyecto
2. Crear branch:
   ```bash
   git checkout -b feature/nueva-funcionalidad
   ```
3. Commit cambios:
   ```bash
   git commit -am 'Add some feature'
   ```
4. Push al branch:
   ```bash
   git push origin feature/nueva-funcionalidad
   ```
5. Abrir Pull Request

## Licencia

GPL V3 - Ver [LICENSE.md](LICENSE.md) para más detalles

© 2025 Jeremías Herminio de Leon Godinez
