package app.project_fin_d_etude.service;

import app.project_fin_d_etude.model.Utilisateur;
import app.project_fin_d_etude.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;

    @Autowired
    public AuthService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Synchronise les informations de l'utilisateur connecté via Keycloak dans
     * la base de données. Si l'utilisateur n'existe pas, il est créé. Sinon,
     * ses informations sont mises à jour.
     *
     * @param email Email de l'utilisateur (provenant de Keycloak)
     * @param nom Nom de l'utilisateur (provenant de Keycloak)
     */
    @Transactional
    public void syncUtilisateurFromKeycloak(String email, String nom) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseGet(() -> {
                    Utilisateur newUser = new Utilisateur();
                    newUser.setEmail(email.toLowerCase().trim());
                    newUser.setDateCreation(LocalDateTime.now());
                    newUser.setActif(true);
                    newUser.setRole(Utilisateur.Role.UTILISATEUR);
                    return newUser;
                });

        if (nom != null && !nom.trim().isEmpty()) {
            utilisateur.setNom(nom.trim());
        }
        utilisateur.setDerniereConnexion(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Récupère un utilisateur par email.
     *
     * @param email Email de l'utilisateur
     * @return Optional de l'utilisateur
     */
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }
}
