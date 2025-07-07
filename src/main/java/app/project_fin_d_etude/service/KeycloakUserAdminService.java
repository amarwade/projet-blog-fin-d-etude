package app.project_fin_d_etude.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.ws.rs.core.Response;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class KeycloakUserAdminService {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakUserAdminService.class);

    @Value("${keycloak.admin.url}")
    private String serverUrl;
    @Value("${keycloak.admin.realm}")
    private String realm;
    @Value("${keycloak.admin.client-id}")
    private String clientId;
    @Value("${keycloak.admin.client-secret}")
    private String clientSecret;

    private Keycloak keycloak;

    @PostConstruct
    public void init() {
        try {
            keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();
            logger.info("Client Keycloak initialisé avec succès pour le realm: {}", realm);
        } catch (Exception e) {
            logger.error("Erreur lors de l'initialisation du client Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible d'initialiser le client Keycloak", e);
        }
    }

    private RealmResource realmResource() {
        return keycloak.realm(realm);
    }

    /**
     * Vérifie que l'utilisateur connecté a le rôle ADMIN.
     *
     * @throws SecurityException si l'utilisateur n'a pas les droits
     * d'administration
     */
    private void checkAdminRights() {
        org.springframework.security.core.Authentication authentication
                = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            logger.warn("Tentative d'accès aux fonctions d'administration par un utilisateur non authentifié");
            throw new SecurityException("Authentification requise pour les opérations d'administration");
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            String userEmail = authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser
                    ? oidcUser.getEmail() : "unknown";
            logger.warn("Tentative d'accès aux fonctions d'administration par un utilisateur non autorisé: {}", userEmail);
            throw new SecurityException("Droits d'administration requis pour cette opération");
        }
    }

    /**
     * Liste tous les utilisateurs Keycloak du realm.
     */
    @Async
    public CompletableFuture<List<UserRepresentation>> listAllUsers() {
        return CompletableFuture.supplyAsync(() -> realmResource().users().list());
    }

    /**
     * Ajoute un utilisateur Keycloak (avec mot de passe), de façon synchrone.
     */
    public String createUser(String username, String email, String password, boolean enabled) {
        checkAdminRights();

        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom d'utilisateur ne peut pas être vide");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être vide");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }

        try {
            UserRepresentation user = new UserRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(enabled);
            user.setEmailVerified(true);

            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(password);
            user.setCredentials(List.of(cred));

            Response response = realmResource().users().create(user);
            if (response.getStatus() == 201) {
                String userId = CreatedResponseUtil.getCreatedId(response);
                logger.info("Utilisateur créé avec succès - username: {}, email: {}, id: {}", username, email, userId);
                return userId;
            } else {
                String errorMsg = "Erreur lors de la création de l'utilisateur: " + response.getStatusInfo().getReasonPhrase();
                logger.error("{} - Status: {}, Username: {}, Email: {}", errorMsg, response.getStatus(), username, email);
                throw new RuntimeException(errorMsg);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création de l'utilisateur {} ({}): {}", username, email, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la création de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Modifie un utilisateur Keycloak (nom, email, activation...), de façon
     * synchrone.
     */
    public void updateUser(String userId, String username, String email, boolean enabled) {
        try {
            UserRepresentation user = realmResource().users().get(userId).toRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(enabled);
            realmResource().users().get(userId).update(user);
            logger.info("Utilisateur mis à jour avec succès - id: {}, username: {}, email: {}", userId, username, email);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de l'utilisateur {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Modifie le mot de passe d'un utilisateur Keycloak, de façon synchrone.
     */
    public void updatePassword(String userId, String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau mot de passe ne peut pas être vide");
        }

        try {
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(newPassword);
            realmResource().users().get(userId).resetPassword(cred);
            logger.info("Mot de passe mis à jour avec succès pour l'utilisateur: {}", userId);
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du mot de passe pour l'utilisateur {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la mise à jour du mot de passe: " + e.getMessage(), e);
        }
    }

    /**
     * Supprime un utilisateur Keycloak, de façon synchrone.
     */
    public void deleteUser(String userId) {
        checkAdminRights();

        try {
            realmResource().users().get(userId).remove();
            logger.info("Utilisateur supprimé avec succès: {}", userId);
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression de l'utilisateur {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur: " + e.getMessage(), e);
        }
    }

    /**
     * Recherche un utilisateur par email.
     */
    @Async
    public CompletableFuture<Optional<UserRepresentation>> findByEmail(String email) {
        return CompletableFuture.supplyAsync(() -> {
            List<UserRepresentation> users = realmResource().users().search(email, true);
            return users.stream().filter(u -> email.equalsIgnoreCase(u.getEmail())).findFirst();
        });
    }

    /**
     * Recherche un utilisateur par username.
     */
    @Async
    public CompletableFuture<Optional<UserRepresentation>> findByUsername(String username) {
        return CompletableFuture.supplyAsync(() -> {
            List<UserRepresentation> users = realmResource().users().search(username, true);
            return users.stream().filter(u -> username.equalsIgnoreCase(u.getUsername())).findFirst();
        });
    }
}
