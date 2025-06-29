package app.project_fin_d_etude.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

/**
 * Utilitaires pour le traitement de texte (troncature, nettoyage, suppression
 * d'accents, etc.).
 */
public class TextUtils {

    /**
     * Tronque un texte en ajoutant des points de suspension s'il dépasse une
     * certaine longueur.
     *
     * @param texte Le texte à tronquer
     * @param maxLength La longueur maximale autorisée
     * @return Le texte tronqué avec "..." si besoin
     */
    public static String resume(String texte, int maxLength) {
        if (texte == null) {
            return "";
        }
        if (maxLength <= 0) {
            return "";
        }
        if (texte.length() <= maxLength) {
            return texte;
        }
        return texte.substring(0, maxLength).trim() + "...";
    }

    /**
     * Nettoie les espaces en trop et supprime les sauts de ligne.
     *
     * @param texte Le texte à nettoyer
     * @return Le texte nettoyé
     */
    public static String clean(String texte) {
        return texte == null ? "" : texte.replaceAll("\\s+", " ").trim();
    }

    /**
     * Supprime les accents d'une chaîne de caractères.
     *
     * @param texte Le texte à traiter
     * @return Le texte sans accents
     */
    public static String removeAccents(String texte) {
        if (texte == null) {
            return "";
        }
        String normalized = Normalizer.normalize(texte, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }
}
