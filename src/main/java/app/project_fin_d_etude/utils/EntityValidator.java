package app.project_fin_d_etude.utils;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service de validation pour les entités métier. Fournit des méthodes de
 * validation spécifiques aux entités.
 */
public final class EntityValidator {

    private static final Logger logger = LoggerFactory.getLogger(EntityValidator.class);
    private static final String ERROR_ENTITY_NULL = "L'entité ne peut pas être null";
    private static final String ERROR_AUTHOR_EMAIL_REQUIRED = "L'email de l'auteur est obligatoire";
    private static final String ERROR_AUTHOR_NAME_REQUIRED = "Le nom de l'auteur est obligatoire";
    private static final String ERROR_POST_REQUIRED = "L'article associé est obligatoire";

    private EntityValidator() {
        // Classe utilitaire, constructeur privé
    }

    /**
     * Valide une entité Post.
     *
     * @param post L'article à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validatePost(Post post) {
        List<String> errors = new ArrayList<>();

        if (post == null) {
            errors.add(ERROR_ENTITY_NULL);
            return ValidationResult.error(errors);
        }

        // Validation du titre
        if (!ValidationUtils.isValidTitle(post.getTitre())) {
            if (post.getTitre() == null || post.getTitre().trim().isEmpty()) {
                errors.add("Le titre de l'article est obligatoire");
            } else {
                errors.add(ValidationUtils.ERROR_TITLE_INVALID);
            }
        }

        // Validation du contenu
        if (!ValidationUtils.isValidContent(post.getContenu())) {
            if (post.getContenu() == null || post.getContenu().trim().isEmpty()) {
                errors.add("Le contenu de l'article est obligatoire");
            } else if (post.getContenu().trim().length() < ValidationUtils.MIN_CONTENT_LENGTH) {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_SHORT);
            } else {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_LONG);
            }
        }

        // Validation de l'auteur
        if (!ValidationUtils.isValidEmail(post.getAuteurEmail())) {
            if (post.getAuteurEmail() == null || post.getAuteurEmail().trim().isEmpty()) {
                errors.add(ERROR_AUTHOR_EMAIL_REQUIRED);
            } else {
                errors.add(ValidationUtils.ERROR_EMAIL_INVALID);
            }
        }

        if (!ValidationUtils.isValidName(post.getAuteurNom())) {
            if (post.getAuteurNom() == null || post.getAuteurNom().trim().isEmpty()) {
                errors.add(ERROR_AUTHOR_NAME_REQUIRED);
            } else {
                errors.add(ValidationUtils.ERROR_NAME_INVALID);
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Valide une entité Message.
     *
     * @param message Le message à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validateMessage(Message message) {
        List<String> errors = new ArrayList<>();

        if (message == null) {
            errors.add(ERROR_ENTITY_NULL);
            return ValidationResult.error(errors);
        }

        // Validation du nom
        if (!ValidationUtils.isValidName(message.getNom())) {
            if (message.getNom() == null || message.getNom().trim().isEmpty()) {
                errors.add("Le nom est obligatoire");
            } else {
                errors.add(ValidationUtils.ERROR_NAME_INVALID);
            }
        }

        // Validation de l'email
        if (!ValidationUtils.isValidEmail(message.getEmail())) {
            if (message.getEmail() == null || message.getEmail().trim().isEmpty()) {
                errors.add("L'email est obligatoire");
            } else {
                errors.add(ValidationUtils.ERROR_EMAIL_INVALID);
            }
        }

        // Validation du sujet
        if (!ValidationUtils.isValidSubject(message.getSujet())) {
            if (message.getSujet() == null || message.getSujet().trim().isEmpty()) {
                errors.add("Le sujet est obligatoire");
            } else {
                errors.add(ValidationUtils.ERROR_SUBJECT_INVALID);
            }
        }

        // Validation du contenu
        if (!ValidationUtils.isValidContent(message.getContenu())) {
            if (message.getContenu() == null || message.getContenu().trim().isEmpty()) {
                errors.add("Le contenu du message est obligatoire");
            } else if (message.getContenu().trim().length() < ValidationUtils.MIN_CONTENT_LENGTH) {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_SHORT);
            } else {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_LONG);
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Valide une entité Commentaire.
     *
     * @param commentaire Le commentaire à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validateCommentaire(Commentaire commentaire) {
        List<String> errors = new ArrayList<>();

        if (commentaire == null) {
            errors.add(ERROR_ENTITY_NULL);
            return ValidationResult.error(errors);
        }

        // Validation du contenu
        if (!ValidationUtils.isValidContent(commentaire.getContenu())) {
            if (commentaire.getContenu() == null || commentaire.getContenu().trim().isEmpty()) {
                errors.add("Le contenu du commentaire est obligatoire");
            } else if (commentaire.getContenu().trim().length() < ValidationUtils.MIN_CONTENT_LENGTH) {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_SHORT);
            } else {
                errors.add(ValidationUtils.ERROR_CONTENT_TOO_LONG);
            }
        }

        // Validation de l'article associé
        if (commentaire.getPost() == null) {
            errors.add(ERROR_POST_REQUIRED);
        }

        // Validation de l'auteur
        if (!ValidationUtils.isValidEmail(commentaire.getAuteurEmail())) {
            if (commentaire.getAuteurEmail() == null || commentaire.getAuteurEmail().trim().isEmpty()) {
                errors.add(ERROR_AUTHOR_EMAIL_REQUIRED);
            } else {
                errors.add(ValidationUtils.ERROR_EMAIL_INVALID);
            }
        }

        if (!ValidationUtils.isValidName(commentaire.getAuteurNom())) {
            if (commentaire.getAuteurNom() == null || commentaire.getAuteurNom().trim().isEmpty()) {
                errors.add(ERROR_AUTHOR_NAME_REQUIRED);
            } else {
                errors.add(ValidationUtils.ERROR_NAME_INVALID);
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Valide une liste d'entités Post.
     *
     * @param posts La liste des articles à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validatePosts(List<Post> posts) {
        if (posts == null) {
            return ValidationResult.error("La liste des articles ne peut pas être null");
        }

        List<String> errors = new ArrayList<>();
        for (int i = 0; i < posts.size(); i++) {
            Post post = posts.get(i);
            ValidationResult result = validatePost(post);
            if (!result.isValid()) {
                errors.add(String.format("Article %d: %s", i + 1, result.getAllErrorsAsString()));
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Valide une liste d'entités Message.
     *
     * @param messages La liste des messages à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validateMessages(List<Message> messages) {
        if (messages == null) {
            return ValidationResult.error("La liste des messages ne peut pas être null");
        }

        List<String> errors = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Message message = messages.get(i);
            ValidationResult result = validateMessage(message);
            if (!result.isValid()) {
                errors.add(String.format("Message %d: %s", i + 1, result.getAllErrorsAsString()));
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Valide une liste d'entités Commentaire.
     *
     * @param commentaires La liste des commentaires à valider
     * @return Résultat de la validation
     */
    public static ValidationResult validateCommentaires(List<Commentaire> commentaires) {
        if (commentaires == null) {
            return ValidationResult.error("La liste des commentaires ne peut pas être null");
        }

        List<String> errors = new ArrayList<>();
        for (int i = 0; i < commentaires.size(); i++) {
            Commentaire commentaire = commentaires.get(i);
            ValidationResult result = validateCommentaire(commentaire);
            if (!result.isValid()) {
                errors.add(String.format("Commentaire %d: %s", i + 1, result.getAllErrorsAsString()));
            }
        }

        return errors.isEmpty() ? ValidationResult.success() : ValidationResult.error(errors);
    }

    /**
     * Classe pour représenter le résultat d'une validation d'entité.
     */
    public static class ValidationResult {

        private final boolean valid;
        private final List<String> errors;

        private ValidationResult(boolean valid, List<String> errors) {
            this.valid = valid;
            this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        }

        /**
         * Retourne un résultat de validation valide (aucune erreur).
         */
        public static ValidationResult success() {
            return new ValidationResult(true, Collections.emptyList());
        }

        /**
         * Retourne un résultat de validation invalide avec un message d'erreur.
         */
        public static ValidationResult error(String error) {
            if (error == null) {
                error = "Erreur de validation inconnue";
            }
            List<String> errors = new ArrayList<>();
            errors.add(error);
            return new ValidationResult(false, errors);
        }

        /**
         * Retourne un résultat de validation invalide avec une liste d'erreurs.
         */
        public static ValidationResult error(List<String> errors) {
            return new ValidationResult(false, errors);
        }

        /**
         * Indique si la validation est réussie.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Retourne la liste des erreurs de validation.
         */
        public List<String> getErrors() {
            return Collections.unmodifiableList(errors);
        }

        /**
         * Retourne la première erreur de validation, ou null s'il n'y en a pas.
         */
        public String getFirstError() {
            return errors.isEmpty() ? null : errors.get(0);
        }

        /**
         * Retourne toutes les erreurs de validation sous forme de chaîne.
         */
        public String getAllErrorsAsString() {
            return String.join("; ", errors);
        }

        /**
         * Retourne le nombre d'erreurs.
         */
        public int getErrorCount() {
            return errors.size();
        }

        /**
         * Indique s'il y a des erreurs.
         */
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
    }
}
