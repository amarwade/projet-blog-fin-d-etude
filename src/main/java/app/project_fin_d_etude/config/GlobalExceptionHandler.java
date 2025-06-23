package app.project_fin_d_etude.config;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Gestionnaire d'exceptions global pour l'application. Intercepte toutes les
 * exceptions non gérées et retourne des réponses HTTP appropriées.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Constantes pour les clés de la réponse
    private static final String KEY_ERROR = "error";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    private static final String KEY_TIMESTAMP = "timestamp";

    /**
     * Gère les exceptions de validation (IllegalArgumentException)
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        logger.warn("Erreur de validation: {}", ex.getMessage());
        return buildResponse("Erreur de validation", ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Gère les exceptions de ressource non trouvée
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(
            jakarta.persistence.EntityNotFoundException ex, WebRequest request) {
        logger.warn("Ressource non trouvée: {}", ex.getMessage());
        return buildResponse("Ressource non trouvée", ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Gère les exceptions d'accès refusé
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        logger.warn("Accès refusé: {}", ex.getMessage());
        return buildResponse("Accès refusé", "Vous n'avez pas les permissions nécessaires pour cette action", HttpStatus.FORBIDDEN);
    }

    /**
     * Gère les exceptions d'authentification
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(
            org.springframework.security.core.AuthenticationException ex, WebRequest request) {
        logger.warn("Erreur d'authentification: {}", ex.getMessage());
        return buildResponse("Erreur d'authentification", "Veuillez vous authentifier pour accéder à cette ressource", HttpStatus.UNAUTHORIZED);
    }

    /**
     * Gère les exceptions de base de données
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(java.sql.SQLException.class)
    public ResponseEntity<Map<String, Object>> handleSQLException(
            java.sql.SQLException ex, WebRequest request) {
        logger.error("Erreur de base de données: {}", ex.getMessage(), ex);
        return buildResponse("Erreur de base de données", "Une erreur s'est produite lors de l'accès aux données", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Gère les exceptions de violation d'intégrité des données
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolationException(
            org.springframework.dao.DataIntegrityViolationException ex, WebRequest request) {
        logger.warn("Violation d'intégrité des données: {}", ex.getMessage());
        return buildResponse("Violation d'intégrité des données", "Cette ressource existe déjà ou ne peut pas être supprimée", HttpStatus.CONFLICT);
    }

    /**
     * Gère toutes les autres exceptions non prévues
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        logger.error("Erreur inattendue: {}", ex.getMessage(), ex);
        return buildResponse("Erreur interne du serveur", "Une erreur inattendue s'est produite", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Méthode utilitaire pour construire la réponse d'erreur.
     */
    private ResponseEntity<Map<String, Object>> buildResponse(String error, String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put(KEY_ERROR, error);
        response.put(KEY_MESSAGE, message);
        response.put(KEY_STATUS, status.value());
        response.put(KEY_TIMESTAMP, System.currentTimeMillis());
        return ResponseEntity.status(status).body(response);
    }
}
