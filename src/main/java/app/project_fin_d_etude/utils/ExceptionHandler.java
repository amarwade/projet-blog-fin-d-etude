package app.project_fin_d_etude.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Classe utilitaire pour centraliser la gestion des exceptions côté service ou
 * logique métier. Fournit des méthodes pour logger, transformer et relayer les
 * erreurs de manière cohérente.
 */
public final class ExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandler.class);

    // Messages d'erreur standardisés (cohérents avec GlobalExceptionHandler)
    public static final String ERROR_GENERIC = "Erreur interne du serveur";
    public static final String ERROR_DATABASE = "Erreur de base de données";
    public static final String ERROR_NETWORK = "Erreur de connexion réseau";
    public static final String ERROR_VALIDATION = "Erreur de validation";
    public static final String ERROR_AUTHENTICATION = "Erreur d'authentification";
    public static final String ERROR_AUTHORIZATION = "Accès refusé";
    public static final String ERROR_NOT_FOUND = "Ressource non trouvée";
    public static final String ERROR_DUPLICATE = "Violation d'intégrité des données";

    private ExceptionHandler() {
        // Classe utilitaire, constructeur privé
    }

    /**
     * Logge et gère une exception de manière générique.
     *
     * @param e L'exception à gérer
     * @param context Contexte de l'erreur (ex: nom du service)
     */
    public static void handleException(Exception e, String context) {
        logger.error("Erreur dans {}: {}", context, e.getMessage(), e);
    }

    /**
     * Logge et gère une exception avec un callback utilisateur.
     *
     * @param e L'exception à gérer
     * @param context Contexte de l'erreur
     * @param errorCallback Callback pour afficher un message utilisateur
     */
    public static void handleException(Exception e, String context, Consumer<String> errorCallback) {
        handleException(e, context);
        String userMessage = getUserFriendlyMessage(e);
        errorCallback.accept(userMessage);
    }

    /**
     * Exécute une opération avec gestion d'erreur et retourne le résultat ou
     * null en cas d'exception.
     *
     * @param operation Opération à exécuter
     * @param context Contexte de l'opération
     * @param errorCallback Callback pour afficher un message utilisateur
     * @return Résultat de l'opération ou null
     */
    public static <T> T executeWithErrorHandling(ThrowingSupplier<T> operation, String context, Consumer<String> errorCallback) {
        try {
            return operation.get();
        } catch (Exception e) {
            handleException(e, context, errorCallback);
            return null;
        }
    }

    /**
     * Exécute une opération void avec gestion d'erreur.
     *
     * @param operation Opération à exécuter
     * @param context Contexte de l'opération
     * @param errorCallback Callback pour afficher un message utilisateur
     */
    public static void executeWithErrorHandling(ThrowingRunnable operation, String context, Consumer<String> errorCallback) {
        try {
            operation.run();
        } catch (Exception e) {
            handleException(e, context, errorCallback);
        }
    }

    /**
     * Retourne un message utilisateur convivial basé sur le type d'exception
     * (cohérent avec GlobalExceptionHandler).
     *
     * @param e L'exception à analyser
     * @return Message utilisateur
     */
    public static String getUserFriendlyMessage(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return ERROR_VALIDATION;
        } else if (e instanceof java.sql.SQLException) {
            return ERROR_DATABASE;
        } else if (e instanceof java.net.ConnectException) {
            return ERROR_NETWORK;
        } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return ERROR_AUTHORIZATION;
        } else if (e instanceof org.springframework.security.core.AuthenticationException) {
            return ERROR_AUTHENTICATION;
        } else if (e instanceof jakarta.persistence.EntityNotFoundException) {
            return ERROR_NOT_FOUND;
        } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
            return ERROR_DUPLICATE;
        } else {
            return ERROR_GENERIC;
        }
    }

    // Interfaces fonctionnelles pour les opérations pouvant lever des exceptions
    /**
     * Interface fonctionnelle pour les opérations qui peuvent lever des
     * exceptions et retourner un résultat.
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {

        T get() throws Exception;
    }

    /**
     * Interface fonctionnelle pour les opérations void qui peuvent lever des
     * exceptions.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {

        void run() throws Exception;
    }

    // Méthodes spécialisées pour des cas d'erreur précis
    /**
     * Gère une erreur de validation.
     *
     * @param field Champ concerné
     * @param message Message d'erreur
     * @param errorCallback Callback utilisateur
     */
    public static void handleValidationError(String field, String message, Consumer<String> errorCallback) {
        String errorMessage = String.format("Erreur de validation pour '%s': %s", field, message);
        logger.warn(errorMessage);
        errorCallback.accept(message);
    }

    /**
     * Gère une erreur de base de données.
     *
     * @param e Exception SQL
     * @param errorCallback Callback utilisateur
     */
    public static void handleDatabaseError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur de base de données: {}", e.getMessage(), e);
        errorCallback.accept(ERROR_DATABASE);
    }

    /**
     * Gère une erreur réseau.
     *
     * @param e Exception réseau
     * @param errorCallback Callback utilisateur
     */
    public static void handleNetworkError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur de réseau: {}", e.getMessage(), e);
        errorCallback.accept(ERROR_NETWORK);
    }

    /**
     * Gère une erreur d'authentification.
     *
     * @param e Exception d'authentification
     * @param errorCallback Callback utilisateur
     */
    public static void handleAuthenticationError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur d'authentification: {}", e.getMessage(), e);
        errorCallback.accept(ERROR_AUTHENTICATION);
    }

    /**
     * Gère une erreur d'autorisation.
     *
     * @param e Exception d'autorisation
     * @param errorCallback Callback utilisateur
     */
    public static void handleAuthorizationError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur d'autorisation: {}", e.getMessage(), e);
        errorCallback.accept(ERROR_AUTHORIZATION);
    }
}
