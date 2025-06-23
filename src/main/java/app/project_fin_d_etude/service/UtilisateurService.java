package app.project_fin_d_etude.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import app.project_fin_d_etude.model.Utilisateur;
import app.project_fin_d_etude.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Recherche un utilisateur par email, de façon asynchrone.
     *
     * @param email L'email de l'utilisateur
     * @return Future contenant un Optional de l'utilisateur
     */
    @Async
    public CompletableFuture<Optional<Utilisateur>> findByEmail(String email) {
        return CompletableFuture.completedFuture(utilisateurRepository.findByEmail(email));
    }

    /**
     * Recherche un utilisateur par identifiant, de façon asynchrone.
     *
     * @param id L'identifiant de l'utilisateur
     * @return Future contenant un Optional de l'utilisateur
     */
    @Async
    public CompletableFuture<Optional<Utilisateur>> findById(Long id) {
        return CompletableFuture.completedFuture(utilisateurRepository.findById(id));
    }

    /**
     * Vérifie l'existence d'un utilisateur par email, de façon asynchrone.
     *
     * @param email L'email à vérifier
     * @return Future contenant true si l'utilisateur existe, sinon false
     */
    @Async
    public CompletableFuture<Boolean> existsByEmail(String email) {
        return CompletableFuture.completedFuture(utilisateurRepository.existsByEmail(email));
    }

    /**
     * Sauvegarde un utilisateur, de façon asynchrone.
     *
     * @param utilisateur L'utilisateur à sauvegarder
     * @return Future contenant l'utilisateur sauvegardé
     */
    @Async
    public CompletableFuture<Utilisateur> save(Utilisateur utilisateur) {
        return CompletableFuture.completedFuture(utilisateurRepository.save(utilisateur));
    }

    /**
     * Supprime un utilisateur, de façon asynchrone.
     *
     * @param utilisateur L'utilisateur à supprimer
     * @return Future complétée une fois la suppression effectuée
     */
    @Async
    public CompletableFuture<Void> delete(Utilisateur utilisateur) {
        utilisateurRepository.delete(utilisateur);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Récupère tous les utilisateurs, de façon asynchrone.
     *
     * @return Future contenant la liste des utilisateurs
     */
    @Async
    public CompletableFuture<List<Utilisateur>> findAll() {
        return CompletableFuture.completedFuture(utilisateurRepository.findAll());
    }

    /**
     * Trouve un utilisateur par OidcUser ou le crée s'il n'existe pas.
     *
     * @param oidcUser L'objet OidcUser de l'utilisateur authentifié
     * @return L'utilisateur persistant
     */
    public Utilisateur findOrCreateAuteur(OidcUser oidcUser) {
        String email = oidcUser.getEmail();
        return utilisateurRepository.findByEmail(email)
                .orElseGet(() -> {
                    Utilisateur newUser = new Utilisateur();
                    newUser.setEmail(email);

                    String nom = oidcUser.getGivenName();
                    String prenom = oidcUser.getFamilyName();
                    String displayName = ((nom != null ? nom : "") + " " + (prenom != null ? prenom : "")).trim();
                    newUser.setNom(displayName.isBlank() ? oidcUser.getPreferredUsername() : displayName);

                    newUser.setActif(true);
                    newUser.setRole(Utilisateur.Role.UTILISATEUR);
                    newUser.setDateCreation(java.time.LocalDateTime.now());
                    newUser.setMotDePasse(""); // Mot de passe non géré localement
                    return utilisateurRepository.save(newUser);
                });
    }
}
