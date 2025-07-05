package app.project_fin_d_etude.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

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
    public static final String ERROR_TIMEOUT = "Délai d'attente dépassé";
    public static final String ERROR_SERVICE_UNAVAILABLE = "Service temporairement indisponible";

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
        if (e == null) {
            logger.warn("Exception null fournie à handleException dans le contexte: {}", context);
            return;
        }
        if (context == null || context.trim().isEmpty()) {
            context = "Contexte inconnu";
        }

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
        if (errorCallback != null) {
            String userMessage = getUserFriendlyMessage(e);
            errorCallback.accept(userMessage);
        }
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
        if (operation == null) {
            logger.warn("Opération null fournie à executeWithErrorHandling dans le contexte: {}", context);
            return null;
        }

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
        if (operation == null) {
            logger.warn("Opération null fournie à executeWithErrorHandling dans le contexte: {}", context);
            return;
        }

        try {
            operation.run();
        } catch (Exception e) {
            handleException(e, context, errorCallback);
        }
    }

    /**
     * Exécute une opération avec gestion d'erreur et retourne le résultat ou
     * une valeur par défaut en cas d'exception.
     *
     * @param operation Opération à exécuter
     * @param defaultValue Valeur par défaut en cas d'erreur
     * @param context Contexte de l'opération
     * @param errorCallback Callback pour afficher un message utilisateur
     * @return Résultat de l'opération ou valeur par défaut
     */
    public static <T> T executeWithErrorHandling(ThrowingSupplier<T> operation, T defaultValue, String context, Consumer<String> errorCallback) {
        if (operation == null) {
            logger.warn("Opération null fournie à executeWithErrorHandling dans le contexte: {}", context);
            return defaultValue;
        }

        try {
            return operation.get();
        } catch (Exception e) {
            handleException(e, context, errorCallback);
            return defaultValue;
        }
    }

    /**
     * Exécute une opération avec gestion d'erreur et retourne un boolean
     * indiquant le succès.
     *
     * @param operation Opération à exécuter
     * @param context Contexte de l'opération
     * @param errorCallback Callback pour afficher un message utilisateur
     * @return true si l'opération a réussi, false sinon
     */
    public static boolean executeWithSuccessHandling(ThrowingRunnable operation, String context, Consumer<String> errorCallback) {
        if (operation == null) {
            logger.warn("Opération null fournie à executeWithSuccessHandling dans le contexte: {}", context);
            return false;
        }

        try {
            operation.run();
            return true;
        } catch (Exception e) {
            handleException(e, context, errorCallback);
            return false;
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
        if (e == null) {
            return ERROR_GENERIC;
        }

        if (e instanceof IllegalArgumentException) {
            return ERROR_VALIDATION;
        } else if (e instanceof java.sql.SQLException) {
            return ERROR_DATABASE;
        } else if (e instanceof java.net.ConnectException || e instanceof java.net.SocketTimeoutException) {
            return ERROR_NETWORK;
        } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return ERROR_AUTHORIZATION;
        } else if (e instanceof org.springframework.security.core.AuthenticationException) {
            return ERROR_AUTHENTICATION;
        } else if (e instanceof jakarta.persistence.EntityNotFoundException) {
            return ERROR_NOT_FOUND;
        } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
            return ERROR_DUPLICATE;
        } else if (e instanceof java.util.concurrent.TimeoutException) {
            return ERROR_TIMEOUT;
        } else if (e instanceof org.springframework.web.client.ResourceAccessException) {
            return ERROR_SERVICE_UNAVAILABLE;
        } else {
            return ERROR_GENERIC;
        }
    }

    /**
     * Retourne un message utilisateur détaillé avec le message original de
     * l'exception.
     *
     * @param e L'exception à analyser
     * @return Message utilisateur détaillé
     */
    public static String getDetailedUserMessage(Exception e) {
        if (e == null) {
            return ERROR_GENERIC;
        }

        String baseMessage = getUserFriendlyMessage(e);
        String detailMessage = e.getMessage();

        if (detailMessage != null && !detailMessage.trim().isEmpty()) {
            return baseMessage + " : " + detailMessage;
        }

        return baseMessage;
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
        String errorMessage = String.format("Erreur de validation pour '%s': %s",
                field != null ? field : "champ inconnu",
                message != null ? message : "erreur inconnue");
        logger.warn(errorMessage);
        if (errorCallback != null) {
            errorCallback.accept(message != null ? message : ERROR_VALIDATION);
        }
    }

    /**
     * Gère une erreur de base de données.
     *
     * @param e Exception SQL
     * @param errorCallback Callback utilisateur
     */
    public static void handleDatabaseError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur de base de données: {}", e != null ? e.getMessage() : "Exception null", e);
        if (errorCallback != null) {
            errorCallback.accept(ERROR_DATABASE);
        }
    }

    /**
     * Gère une erreur réseau.
     *
     * @param e Exception réseau
     * @param errorCallback Callback utilisateur
     */
    public static void handleNetworkError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur de réseau: {}", e != null ? e.getMessage() : "Exception null", e);
        if (errorCallback != null) {
            errorCallback.accept(ERROR_NETWORK);
        }
    }

    /**
     * Gère une erreur d'authentification.
     *
     * @param e Exception d'authentification
     * @param errorCallback Callback utilisateur
     */
    public static void handleAuthenticationError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur d'authentification: {}", e != null ? e.getMessage() : "Exception null", e);
        if (errorCallback != null) {
            errorCallback.accept(ERROR_AUTHENTICATION);
        }
    }

    /**
     * Gère une erreur d'autorisation.
     *
     * @param e Exception d'autorisation
     * @param errorCallback Callback utilisateur
     */
    public static void handleAuthorizationError(Exception e, Consumer<String> errorCallback) {
        logger.error("Erreur d'autorisation: {}", e != null ? e.getMessage() : "Exception null", e);
        if (errorCallback != null) {
            errorCallback.accept(ERROR_AUTHORIZATION);
        }
    }
}
