package app.project_fin_d_etude.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class KeycloakUserAdminService {

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
        keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }

    private RealmResource realmResource() {
        return keycloak.realm(realm);
    }

    /**
     * Liste tous les utilisateurs Keycloak du realm.
     */
    @Async
    public CompletableFuture<List<UserRepresentation>> listAllUsers() {
        return CompletableFuture.supplyAsync(() -> realmResource().users().list());
    }

    /**
     * Ajoute un utilisateur Keycloak (avec mot de passe).
     */
    @Async
    public CompletableFuture<String> createUser(String username, String email, String password, boolean enabled) {
        return CompletableFuture.supplyAsync(() -> {
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
                return CreatedResponseUtil.getCreatedId(response);
            } else {
                throw new RuntimeException("Erreur lors de la création de l'utilisateur : " + response.getStatusInfo().getReasonPhrase());
            }
        });
    }

    /**
     * Modifie un utilisateur Keycloak (nom, email, activation...)
     */
    @Async
    public CompletableFuture<Void> updateUser(String userId, String username, String email, boolean enabled) {
        return CompletableFuture.runAsync(() -> {
            UserRepresentation user = realmResource().users().get(userId).toRepresentation();
            user.setUsername(username);
            user.setEmail(email);
            user.setEnabled(enabled);
            realmResource().users().get(userId).update(user);
        });
    }

    /**
     * Modifie le mot de passe d'un utilisateur Keycloak.
     */
    @Async
    public CompletableFuture<Void> updatePassword(String userId, String newPassword) {
        return CompletableFuture.runAsync(() -> {
            CredentialRepresentation cred = new CredentialRepresentation();
            cred.setTemporary(false);
            cred.setType(CredentialRepresentation.PASSWORD);
            cred.setValue(newPassword);
            realmResource().users().get(userId).resetPassword(cred);
        });
    }

    /**
     * Supprime un utilisateur Keycloak.
     */
    @Async
    public CompletableFuture<Void> deleteUser(String userId) {
        return CompletableFuture.runAsync(() -> realmResource().users().get(userId).remove());
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
