package app.project_fin_d_etude.repository;

import app.project_fin_d_etude.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email L'email de l'utilisateur
     * @return Un Optional contenant l'utilisateur s'il existe
     */
    Optional<Utilisateur> findByEmail(String email);

    /**
     * Vérifie l'existence d'un utilisateur par email.
     *
     * @param email L'email à vérifier
     * @return true si un utilisateur existe avec cet email, sinon false
     */
    boolean existsByEmail(String email);
}
