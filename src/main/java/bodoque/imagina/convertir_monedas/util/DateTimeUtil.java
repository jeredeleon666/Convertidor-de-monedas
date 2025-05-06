package bodoque.imagina.convertir_monedas.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utilidad para manejar fechas y horas en formato ISO 8601.
 */
public class DateTimeUtil {
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(ISO_FORMAT);

    /**
     * Convierte un LocalDateTime a String en formato ISO 8601.
     * @param dateTime La fecha y hora a convertir
     * @return String con la fecha y hora en formato ISO 8601
     */
    public static String localDateTimeToString(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(FORMATTER);
    }

    /**
     * Convierte un String en formato ISO 8601 a LocalDateTime.
     * @param dateTimeStr La cadena que contiene la fecha y hora
     * @return LocalDateTime convertido
     * @throws DateTimeParseException Si el formato no es válido
     */
    public static LocalDateTime stringToLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, FORMATTER);
    }

    /**
     * Obtiene la fecha y hora actual en formato LocalDateTime.
     * @return LocalDateTime con la fecha y hora actual
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Compara dos fechas para determinar si la primera es después de la segunda.
     * @param date1 Primera fecha
     * @param date2 Segunda fecha
     * @return true si date1 es después de date2, false en caso contrario
     */
    public static boolean isAfter(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isAfter(date2);
    }

    /**
     * Compara dos fechas para determinar si la primera es antes de la segunda.
     * @param date1 Primera fecha
     * @param date2 Segunda fecha
     * @return true si date1 es antes de date2, false en caso contrario
     */
    public static boolean isBefore(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.isBefore(date2);
    }

    /**
     * Formatea un LocalDateTime para mostrar en la interfaz de usuario.
     * @param dateTime La fecha y hora a formatear
     * @return String con formato "dd/MM/yyyy HH:mm:ss"
     */
    public static String formatForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(displayFormatter);
    }
}