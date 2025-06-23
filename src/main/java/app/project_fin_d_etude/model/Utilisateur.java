package app.project_fin_d_etude.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "utilisateurs")
public class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email unique de l'utilisateur.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Mot de passe (non utilisé si authentification Keycloak).
     */
    @Column(nullable = true)
    private String motDePasse;

    /**
     * Nom de l'utilisateur.
     */
    @Column(nullable = false)
    private String nom;

    /**
     * Date de création du compte utilisateur.
     */
    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation;

    /**
     * Date de dernière connexion de l'utilisateur.
     */
    @Column(name = "derniere_connexion")
    private LocalDateTime derniereConnexion;

    /**
     * Statut d'activation du compte utilisateur.
     */
    @Column(nullable = false)
    private boolean actif = true;

    /**
     * Rôle de l'utilisateur (ADMIN ou UTILISATEUR).
     */
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role = Role.UTILISATEUR;

    public enum Role {
        ADMIN,
        UTILISATEUR
    }
}
