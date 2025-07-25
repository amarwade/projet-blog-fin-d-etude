package app.project_fin_d_etude.utils;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Classe utilitaire pour centraliser toutes les validations de formulaires.
 * Fournit des méthodes de validation réutilisables et cohérentes.
 */
public final class ValidationUtils {

    private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

    // Patterns de validation optimisés
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            Pattern.CASE_INSENSITIVE
    );
    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[a-zA-ZÀ-ÿ\\s'-]{2,50}$",
            Pattern.UNICODE_CHARACTER_CLASS
    );
    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "^[a-zA-ZÀ-ÿ0-9\\s'-]{3,300}$",
            Pattern.UNICODE_CHARACTER_CLASS
    );
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[+]?[0-9\\s()-]{10,15}$"
    );
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$",
            Pattern.CASE_INSENSITIVE
    );

    // Messages d'erreur standardisés
    public static final String ERROR_FIELD_REQUIRED = "Ce champ est obligatoire";
    public static final String ERROR_EMAIL_INVALID = "Veuillez entrer une adresse email valide";
    public static final String ERROR_NAME_INVALID = "Le nom doit contenir entre 2 et 50 caractères (lettres, espaces, tirets, apostrophes)";
    public static final String ERROR_TITLE_INVALID = "Le titre doit contenir entre 3 et 300 caractères";
    public static final String ERROR_CONTENT_TOO_SHORT = "Le contenu doit contenir au moins 10 caractères";
    public static final String ERROR_CONTENT_TOO_LONG = "Le contenu ne peut pas dépasser 5000 caractères";
    public static final String ERROR_SUBJECT_INVALID = "Le sujet doit contenir entre 3 et 100 caractères";
    public static final String ERROR_PHONE_INVALID = "Veuillez entrer un numéro de téléphone valide";
    public static final String ERROR_URL_INVALID = "Veuillez entrer une URL valide";

    // Constantes de validation
    public static final int MIN_NAME_LENGTH = 2;
    public static final int MAX_NAME_LENGTH = 50;
    public static final int MIN_TITLE_LENGTH = 3;
    public static final int MAX_TITLE_LENGTH = 200;
    public static final int MIN_CONTENT_LENGTH = 10;
    public static final int MAX_CONTENT_LENGTH = 5000;
    public static final int MIN_SUBJECT_LENGTH = 3;
    public static final int MAX_SUBJECT_LENGTH = 100;
    public static final int MIN_PHONE_LENGTH = 10;
    public static final int MAX_PHONE_LENGTH = 15;

    private ValidationUtils() {
        // Classe utilitaire, constructeur privé
    }

    /**
     * Valide qu'un champ TextField n'est pas vide.
     */
    public static boolean isFieldNotEmpty(TextField field) {
        return field != null && field.getValue() != null && !field.getValue().trim().isEmpty();
    }

    /**
     * Valide qu'un champ TextArea n'est pas vide.
     */
    public static boolean isFieldNotEmpty(TextArea field) {
        return field != null && field.getValue() != null && !field.getValue().trim().isEmpty();
    }

    /**
     * Valide qu'une chaîne n'est pas vide.
     */
    public static boolean isStringNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * Valide qu'une chaîne n'est pas null et a une longueur minimale.
     */
    public static boolean isStringNotEmpty(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }

    /**
     * Valide une adresse email.
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Valide un nom (prénom, nom de famille).
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Valide un titre d'article.
     */
    public static boolean isValidTitle(String title) {
        return title != null && TITLE_PATTERN.matcher(title.trim()).matches();
    }

    /**
     * Valide le contenu d'un article ou commentaire.
     */
    public static boolean isValidContent(String content) {
        if (content == null) {
            return false;
        }
        String trimmed = content.trim();
        return trimmed.length() >= MIN_CONTENT_LENGTH && trimmed.length() <= MAX_CONTENT_LENGTH;
    }

    /**
     * Valide un sujet de message.
     */
    public static boolean isValidSubject(String subject) {
        if (subject == null) {
            return false;
        }
        String trimmed = subject.trim();
        return trimmed.length() >= MIN_SUBJECT_LENGTH && trimmed.length() <= MAX_SUBJECT_LENGTH;
    }

    /**
     * Valide un numéro de téléphone.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Valide une URL.
     */
    public static boolean isValidUrl(String url) {
        return url != null && URL_PATTERN.matcher(url.trim()).matches();
    }

    /**
     * Valide la longueur d'une chaîne.
     */
    public static boolean isValidLength(String value, int minLength, int maxLength) {
        if (value == null) {
            return false;
        }
        int length = value.trim().length();
        return length >= minLength && length <= maxLength;
    }

    /**
     * Valide qu'une chaîne ne dépasse pas une longueur maximale.
     */
    public static boolean isNotTooLong(String value, int maxLength) {
        return value == null || value.trim().length() <= maxLength;
    }

    /**
     * Valide qu'une chaîne a au moins une longueur minimale.
     */
    public static boolean isNotTooShort(String value, int minLength) {
        return value != null && value.trim().length() >= minLength;
    }

    /**
     * Valide un champ TextField avec un message d'erreur personnalisé.
     */
    public static ValidationResult validateTextField(TextField field, String errorMessage) {
        if (!isFieldNotEmpty(field)) {
            return ValidationResult.error(errorMessage != null ? errorMessage : ERROR_FIELD_REQUIRED);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ TextArea avec un message d'erreur personnalisé.
     */
    public static ValidationResult validateTextArea(TextArea field, String errorMessage) {
        if (!isFieldNotEmpty(field)) {
            return ValidationResult.error(errorMessage != null ? errorMessage : ERROR_FIELD_REQUIRED);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ email.
     */
    public static ValidationResult validateEmail(TextField emailField) {
        if (!isFieldNotEmpty(emailField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidEmail(emailField.getValue())) {
            return ValidationResult.error(ERROR_EMAIL_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ nom.
     */
    public static ValidationResult validateName(TextField nameField) {
        if (!isFieldNotEmpty(nameField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidName(nameField.getValue())) {
            return ValidationResult.error(ERROR_NAME_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ titre.
     */
    public static ValidationResult validateTitle(TextField titleField) {
        if (!isFieldNotEmpty(titleField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidTitle(titleField.getValue())) {
            return ValidationResult.error(ERROR_TITLE_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ contenu.
     */
    public static ValidationResult validateContent(TextArea contentField) {
        if (!isFieldNotEmpty(contentField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidContent(contentField.getValue())) {
            if (contentField.getValue().trim().length() < MIN_CONTENT_LENGTH) {
                return ValidationResult.error(ERROR_CONTENT_TOO_SHORT);
            } else {
                return ValidationResult.error(ERROR_CONTENT_TOO_LONG);
            }
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ sujet.
     */
    public static ValidationResult validateSubject(TextField subjectField) {
        if (!isFieldNotEmpty(subjectField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidSubject(subjectField.getValue())) {
            return ValidationResult.error(ERROR_SUBJECT_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ téléphone.
     */
    public static ValidationResult validatePhone(TextField phoneField) {
        if (!isFieldNotEmpty(phoneField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidPhone(phoneField.getValue())) {
            return ValidationResult.error(ERROR_PHONE_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ URL.
     */
    public static ValidationResult validateUrl(TextField urlField) {
        if (!isFieldNotEmpty(urlField)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidUrl(urlField.getValue())) {
            return ValidationResult.error(ERROR_URL_INVALID);
        }
        return ValidationResult.success();
    }

    /**
     * Valide un champ avec une longueur personnalisée.
     */
    public static ValidationResult validateLength(TextField field, int minLength, int maxLength, String errorMessage) {
        if (!isFieldNotEmpty(field)) {
            return ValidationResult.error(ERROR_FIELD_REQUIRED);
        }
        if (!isValidLength(field.getValue(), minLength, maxLength)) {
            return ValidationResult.error(errorMessage != null ? errorMessage
                    : String.format("Le champ doit contenir entre %d et %d caractères", minLength, maxLength));
        }
        return ValidationResult.success();
    }

    /**
     * Nettoie et valide une chaîne de caractères.
     */
    public static String cleanAndValidate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        String cleaned = value.trim();
        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        return cleaned;
    }

    /**
     * Classe pour représenter le résultat d'une validation.
     */
    public static class ValidationResult {

        private final boolean valid;
        private final String errorMessage;

        private ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        /**
         * Retourne un résultat de validation valide (aucune erreur).
         */
        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        /**
         * Retourne un résultat de validation invalide avec un message d'erreur.
         */
        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        /**
         * Indique si la validation est réussie.
         */
        public boolean isValid() {
            return valid;
        }

        /**
         * Retourne le message d'erreur de validation, ou null si aucune erreur.
         */
        public String getErrorMessage() {
            return errorMessage;
        }
    }

    // Pour i18n, prévoir une méthode getMessage(String key) à l'avenir
}
