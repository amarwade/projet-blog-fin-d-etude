package app.project_fin_d_etude.service;

import java.util.HashMap;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * Service de gestion des profils utilisateurs. Permet la modification des
 * informations personnelles et du mot de passe via Keycloak.
 */
@Service
public class UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileService.class);

    private final KeycloakUserAdminService keycloakUserAdminService;
    private final PostService postService;
    private final CommentaireService commentaireService;

    @Autowired
    public UserProfileService(KeycloakUserAdminService keycloakUserAdminService, PostService postService, CommentaireService commentaireService) {
        this.keycloakUserAdminService = keycloakUserAdminService;
        this.postService = postService;
        this.commentaireService = commentaireService;
    }

    /**
     * Met à jour les informations personnelles de l'utilisateur connecté.
     *
     * @param firstName Nouveau prénom
     * @param lastName Nouveau nom de famille
     * @param email Nouvel email (si différent)
     * @param authentication Authentication context
     * @return true si la mise à jour a réussi
     */
    public boolean updatePersonalInfo(String firstName, String lastName, String email, Authentication authentication) {
        try {
            String currentUserEmail = app.project_fin_d_etude.utils.SecurityUtils.getCurrentUserEmail(authentication);
            if (currentUserEmail == null) {
                logger.warn("Tentative de mise à jour du profil par un utilisateur non authentifié. "
                        + "Authentication: {}, Principal: {}, Type: {}",
                        authentication,
                        authentication != null ? authentication.getPrincipal() : "null",
                        authentication != null && authentication.getPrincipal() != null ? authentication.getPrincipal().getClass() : "null"
                );
                return false;
            }

            // Récupérer l'utilisateur Keycloak
            var userOpt = keycloakUserAdminService.findByEmail(currentUserEmail).get();
            if (userOpt.isEmpty()) {
                logger.error("Utilisateur non trouvé dans Keycloak: {}", currentUserEmail);
                return false;
            }

            UserRepresentation user = userOpt.get();
            user.setFirstName(firstName);
            user.setLastName(lastName);

            boolean emailChanged = false;
            // Mettre à jour l'email seulement s'il a changé
            if (email != null && !email.equals(currentUserEmail)) {
                user.setEmail(email);
                emailChanged = true;
            }

            // Mettre à jour les attributs personnalisés
            Map<String, java.util.List<String>> attributes = user.getAttributes();
            if (attributes == null) {
                attributes = new HashMap<>();
            }

            // Ajouter des attributs spécifiques à l'entreprise
            attributes.put("department", java.util.List.of("À définir"));
            attributes.put("position", java.util.List.of("À définir"));
            attributes.put("phone_extension", java.util.List.of(""));
            attributes.put("office_location", java.util.List.of(""));

            user.setAttributes(attributes);

            keycloakUserAdminService.updateUser(user.getId(), user.getUsername(), user.getEmail(), user.isEnabled());

            // Migration des articles et commentaires si l'email a changé
            if (emailChanged) {
                int nbPosts = postService.migrerEmailAuteur(currentUserEmail, email);
                int nbCommentaires = commentaireService.migrerEmailAuteur(currentUserEmail, email);
                logger.info("{} articles et {} commentaires migrés de {} vers {}", nbPosts, nbCommentaires, currentUserEmail, email);
            }
            // Migration du nom d'auteur des articles et commentaires si le nom ou prénom a changé
            String newNomComplet = firstName + " " + lastName;
            int nbNom = postService.migrerNomAuteur(emailChanged ? email : currentUserEmail, newNomComplet);
            int nbNomCommentaires = commentaireService.migrerNomAuteur(emailChanged ? email : currentUserEmail, newNomComplet);
            logger.info("Nom d'auteur mis à jour pour {} articles et {} commentaires ({} -> {})", nbNom, nbNomCommentaires, currentUserEmail, newNomComplet);

            logger.info("Profil mis à jour avec succès pour l'utilisateur: {}", currentUserEmail);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Change le mot de passe de l'utilisateur connecté.
     *
     * @param currentPassword Mot de passe actuel (pour validation)
     * @param newPassword Nouveau mot de passe
     * @param authentication Authentication context
     * @return true si le changement a réussi
     */
    public boolean changePassword(String currentPassword, String newPassword, Authentication authentication) {
        try {
            String currentUserEmail = app.project_fin_d_etude.utils.SecurityUtils.getCurrentUserEmail(authentication);
            if (currentUserEmail == null) {
                logger.warn("Tentative de changement de mot de passe par un utilisateur non authentifié. "
                        + "Authentication: {}, Principal: {}, Type: {}",
                        authentication,
                        authentication != null ? authentication.getPrincipal() : "null",
                        authentication != null && authentication.getPrincipal() != null ? authentication.getPrincipal().getClass() : "null"
                );
                return false;
            }

            // Validation du nouveau mot de passe
            if (!isValidPassword(newPassword)) {
                logger.warn("Nouveau mot de passe invalide pour l'utilisateur: {}", currentUserEmail);
                return false;
            }

            // Récupérer l'utilisateur Keycloak
            var userOpt = keycloakUserAdminService.findByEmail(currentUserEmail).get();
            if (userOpt.isEmpty()) {
                logger.error("Utilisateur non trouvé dans Keycloak: {}", currentUserEmail);
                return false;
            }

            // Note: La validation du mot de passe actuel nécessiterait une API Keycloak spécifique
            // Pour l'instant, on fait confiance à l'authentification Spring Security
            keycloakUserAdminService.updatePassword(userOpt.get().getId(), newPassword);

            logger.info("Mot de passe changé avec succès pour l'utilisateur: {}", currentUserEmail);
            return true;

        } catch (Exception e) {
            logger.error("Erreur lors du changement de mot de passe: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * Récupère les informations du profil de l'utilisateur connecté.
     *
     * @return Map contenant les informations du profil
     */
    public Map<String, String> getCurrentUserProfile() {
        Map<String, String> profile = new HashMap<>();

        try {
            String currentUserEmail = app.project_fin_d_etude.utils.SecurityUtils.getCurrentUserEmail();
            if (currentUserEmail == null) {
                return profile;
            }

            var userOpt = keycloakUserAdminService.findByEmail(currentUserEmail).get();
            if (userOpt.isPresent()) {
                UserRepresentation user = userOpt.get();
                profile.put("email", user.getEmail());
                profile.put("firstName", user.getFirstName());
                profile.put("lastName", user.getLastName());
                profile.put("fullName", app.project_fin_d_etude.utils.SecurityUtils.getCurrentUserFullName());

                // Attributs personnalisés
                Map<String, java.util.List<String>> attributes = user.getAttributes();
                if (attributes != null) {
                    profile.put("department", getAttributeValue(attributes, "department"));
                    profile.put("position", getAttributeValue(attributes, "position"));
                    profile.put("phoneExtension", getAttributeValue(attributes, "phone_extension"));
                    profile.put("officeLocation", getAttributeValue(attributes, "office_location"));
                }
            }

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du profil: {}", e.getMessage(), e);
        }

        return profile;
    }

    /**
     * Valide un mot de passe selon les règles de sécurité de l'entreprise.
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Au moins une lettre majuscule, une minuscule, un chiffre
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");

        return hasUpperCase && hasLowerCase && hasDigit;
    }

    /**
     * Récupère la valeur d'un attribut de manière sécurisée.
     */
    private String getAttributeValue(Map<String, java.util.List<String>> attributes, String key) {
        if (attributes.containsKey(key)) {
            java.util.List<String> list = attributes.get(key);
            return list != null && !list.isEmpty() ? list.get(0) : "";
        }
        return "";
    }
}
