package app.project_fin_d_etude.presenter;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.service.UserProfileService;
import lombok.Setter;

/**
 * Présentateur pour la gestion des profils utilisateurs. Gère la modification
 * des informations personnelles et du mot de passe.
 */
@Component
public class UserProfilePresenter {

    private static final Logger logger = LoggerFactory.getLogger(UserProfilePresenter.class);

    @Setter
    private UserProfileView view;

    private final UserProfileService userProfileService;

    @Autowired
    public UserProfilePresenter(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    /**
     * Interface à implémenter par la vue pour lier le présentateur.
     */
    public interface UserProfileView {

        void afficherProfil(Map<String, String> profile);

        void afficherMessage(String message);

        void afficherErreur(String erreur);

        void viderFormulaire();

        void rafraichirProfil();
    }

    /**
     * Charge le profil de l'utilisateur connecté de manière asynchrone.
     */
    public void chargerProfil() {
        if (view == null) {
            return;
        }
        UserProfileView currentView = this.view;

        handleAsyncOperation(
                CompletableFuture.supplyAsync(userProfileService::getCurrentUserProfile),
                "Erreur lors du chargement du profil",
                profile -> {
                    if (profile.isEmpty()) {
                        currentView.afficherErreur("Impossible de récupérer les informations du profil");
                    } else {
                        currentView.afficherProfil(profile);
                    }
                }
        );
    }

    /**
     * Met à jour les informations personnelles de l'utilisateur.
     */
    public void mettreAJourProfil(String firstName, String lastName, String email) {
        if (view == null) {
            return;
        }
        UserProfileView currentView = this.view;

        // Capture l'authentification dans le thread principal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Validation des champs
        if (firstName == null || firstName.trim().isEmpty()) {
            currentView.afficherErreur("Le prénom est obligatoire");
            return;
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            currentView.afficherErreur("Le nom de famille est obligatoire");
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            currentView.afficherErreur("L'email est obligatoire");
            return;
        }

        handleAsyncOperation(
                CompletableFuture.supplyAsync(() -> {
                    // Utilise l'auth capturée, pas celle du thread asynchrone
                    return Boolean.valueOf(userProfileService.updatePersonalInfo(firstName, lastName, email, auth));
                }),
                "Erreur lors de la mise à jour du profil",
                success -> {
                    if (success) {
                        currentView.afficherMessage("Profil mis à jour avec succès");
                        currentView.viderFormulaire();
                        currentView.rafraichirProfil();
                    } else {
                        currentView.afficherErreur("Impossible de mettre à jour le profil");
                    }
                }
        );
    }

    /**
     * Change le mot de passe de l'utilisateur.
     */
    public void changerMotDePasse(String currentPassword, String newPassword, String confirmPassword) {
        if (view == null) {
            return;
        }
        UserProfileView currentView = this.view;

        // Capture l'authentification dans le thread principal
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Validation des champs
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            currentView.afficherErreur("Le mot de passe actuel est obligatoire");
            return;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            currentView.afficherErreur("Le nouveau mot de passe est obligatoire");
            return;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            currentView.afficherErreur("La confirmation du mot de passe est obligatoire");
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            currentView.afficherErreur("Les mots de passe ne correspondent pas");
            return;
        }

        handleAsyncOperation(
                CompletableFuture.supplyAsync(() -> {
                    // Utilise l'auth capturée, pas celle du thread asynchrone
                    return Boolean.valueOf(userProfileService.changePassword(currentPassword, newPassword, auth));
                }),
                "Erreur lors du changement de mot de passe",
                success -> {
                    if (success) {
                        currentView.afficherMessage("Mot de passe changé avec succès");
                        currentView.viderFormulaire();
                    } else {
                        currentView.afficherErreur("Impossible de changer le mot de passe");
                    }
                }
        );
    }

    /**
     * Met à jour les informations personnelles de l'utilisateur (synchrone).
     */
    public boolean mettreAJourProfilSynchrone(String firstName, String lastName, String email) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = app.project_fin_d_etude.utils.SecurityUtils.getCurrentUserEmail(auth);
        boolean emailChanged = email != null && !email.equals(currentUserEmail);
        // Validation : seul l'email est obligatoire
        if (email == null || email.trim().isEmpty()) {
            if (view != null) {
                view.afficherErreur("L'email est obligatoire");
            }
            return false;
        }
        // Les champs prénom et nom ne sont plus utilisés, on passe des valeurs vides
        boolean result = userProfileService.updatePersonalInfo("", "", email, auth);
        if (result && emailChanged && view != null) {
            view.afficherMessage("Votre email a été modifié. Veuillez vous déconnecter puis vous reconnecter pour voir les nouvelles informations dans votre profil.");
        }
        return result;
    }

    /**
     * Change le mot de passe de l'utilisateur (synchrone).
     */
    public boolean changerMotDePasseSynchrone(String currentPassword, String newPassword, String confirmPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Validation des champs
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            if (view != null) {
                view.afficherErreur("Le mot de passe actuel est obligatoire");
            }
            return false;
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            if (view != null) {
                view.afficherErreur("Le nouveau mot de passe est obligatoire");
            }
            return false;
        }
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            if (view != null) {
                view.afficherErreur("La confirmation du mot de passe est obligatoire");
            }
            return false;
        }
        if (!newPassword.equals(confirmPassword)) {
            if (view != null) {
                view.afficherErreur("Les mots de passe ne correspondent pas");
            }
            return false;
        }
        return userProfileService.changePassword(currentPassword, newPassword, auth);
    }

    /**
     * Méthode utilitaire pour gérer les opérations asynchrones.
     */
    private <T> void handleAsyncOperation(
            CompletableFuture<T> future,
            String errorMessage,
            Consumer<T> onSuccess
    ) {
        future.whenComplete((result, ex) -> {
            UI.getCurrent().access(() -> {
                if (ex != null) {
                    logger.error("{}: {}", errorMessage, ex.getMessage(), ex);
                    if (view != null) {
                        view.afficherErreur("Une erreur est survenue lors de l'opération");
                    }
                } else {
                    onSuccess.accept(result);
                }
            });
        });
    }
}
