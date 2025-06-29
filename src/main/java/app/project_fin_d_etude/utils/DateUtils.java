package app.project_fin_d_etude.utils;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Utilitaires pour le formatage et la manipulation des dates dans
 * l'application.
 */
public final class DateUtils {

    private static final DateTimeFormatter FORMAT_STANDARD
            = DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm", Locale.FRENCH);
    private static final DateTimeFormatter FORMAT_DATE_ONLY
            = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter FORMAT_TIME_ONLY
            = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH);
    private static final DateTimeFormatter FORMAT_SHORT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH);
    private static final DateTimeFormatter FORMAT_ISO
            = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private DateUtils() {
        // Classe utilitaire, constructeur privé
    }

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

    /**
     * Formate un LocalDateTime selon le format date seulement.
     *
     * @param dateTime La date à formater
     * @return La date formatée en chaîne, ou une chaîne vide si null
     */
    public static String formatDateOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMAT_DATE_ONLY);
    }

    /**
     * Formate un LocalDateTime selon le format heure seulement.
     *
     * @param dateTime La date à formater
     * @return L'heure formatée en chaîne, ou une chaîne vide si null
     */
    public static String formatTimeOnly(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMAT_TIME_ONLY);
    }

    /**
     * Formate un LocalDateTime selon le format court.
     *
     * @param dateTime La date à formater
     * @return La date formatée en chaîne, ou une chaîne vide si null
     */
    public static String formatShort(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMAT_SHORT);
    }

    /**
     * Formate un LocalDateTime selon le format ISO.
     *
     * @param dateTime La date à formater
     * @return La date formatée en chaîne ISO, ou une chaîne vide si null
     */
    public static String formatIso(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(FORMAT_ISO);
    }

    /**
     * Formate un LocalDate selon le format standard.
     *
     * @param date La date à formater
     * @return La date formatée en chaîne, ou une chaîne vide si null
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMAT_DATE_ONLY);
    }

    /**
     * Formate un LocalDate selon le format court.
     *
     * @param date La date à formater
     * @return La date formatée en chaîne, ou une chaîne vide si null
     */
    public static String formatShort(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(FORMAT_SHORT);
    }

    /**
     * Calcule la différence en jours entre deux dates.
     *
     * @param date1 La première date
     * @param date2 La deuxième date
     * @return Le nombre de jours entre les deux dates
     */
    public static long daysBetween(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(date1, date2);
    }

    /**
     * Calcule la différence en heures entre deux dates.
     *
     * @param date1 La première date
     * @param date2 La deuxième date
     * @return Le nombre d'heures entre les deux dates
     */
    public static long hoursBetween(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(date1, date2);
    }

    /**
     * Calcule la différence en minutes entre deux dates.
     *
     * @param date1 La première date
     * @param date2 La deuxième date
     * @return Le nombre de minutes entre les deux dates
     */
    public static long minutesBetween(LocalDateTime date1, LocalDateTime date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(date1, date2);
    }

    /**
     * Vérifie si une date est dans le passé.
     *
     * @param dateTime La date à vérifier
     * @return true si la date est dans le passé, false sinon
     */
    public static boolean isInPast(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Vérifie si une date est dans le futur.
     *
     * @param dateTime La date à vérifier
     * @return true si la date est dans le futur, false sinon
     */
    public static boolean isInFuture(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        return dateTime.isAfter(LocalDateTime.now());
    }

    /**
     * Vérifie si une date est aujourd'hui.
     *
     * @param dateTime La date à vérifier
     * @return true si la date est aujourd'hui, false sinon
     */
    public static boolean isToday(LocalDateTime dateTime) {
        if (dateTime == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        return dateTime.toLocalDate().equals(today);
    }

    /**
     * Retourne une description relative du temps écoulé (ex: "il y a 2
     * heures").
     *
     * @param dateTime La date à analyser
     * @return La description relative du temps
     */
    public static String getRelativeTimeDescription(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);

        if (minutes < 1) {
            return "À l'instant";
        } else if (minutes < 60) {
            return "Il y a " + minutes + " minute" + (minutes > 1 ? "s" : "");
        } else if (hours < 24) {
            return "Il y a " + hours + " heure" + (hours > 1 ? "s" : "");
        } else if (days < 7) {
            return "Il y a " + days + " jour" + (days > 1 ? "s" : "");
        } else {
            return format(dateTime);
        }
    }

    /**
     * Retourne la date et l'heure actuelles.
     *
     * @return La date et l'heure actuelles
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * Retourne la date actuelle.
     *
     * @return La date actuelle
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
}
