package app.project_fin_d_etude.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaires pour le formatage des dates dans l'application.
 */
public class DateUtils {

    private static final DateTimeFormatter FORMAT_STANDARD
            = DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm");

    /**
     * Formate un LocalDateTime selon le format standard de l'application.
     *
     * @param dateTime La date à formater
     * @return La date formatée en chaîne, ou une chaîne vide si null
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMAT_STANDARD);
    }
}
