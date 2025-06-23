package app.project_fin_d_etude.utils;

/**
 * Utilitaires pour le traitement de texte (troncature, nettoyage, etc.).
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
}
