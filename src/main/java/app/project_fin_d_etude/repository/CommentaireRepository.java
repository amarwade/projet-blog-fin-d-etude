package app.project_fin_d_etude.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    /**
     * Récupère la liste des commentaires associés à un post donné.
     *
     * @param post Le post concerné
     * @return Liste des commentaires du post
     */
    List<Commentaire> findByPost(Post post);

    /**
     * Récupère la liste des réponses à un commentaire donné.
     *
     * @param parent Le commentaire parent
     * @return Liste des réponses au commentaire
     */
    List<Commentaire> findByParent(Commentaire parent);

    /**
     * Met à jour l'email de l'auteur pour tous ses commentaires.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.auteurEmail = :newEmail WHERE c.auteurEmail = :oldEmail")
    int updateAuteurEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    /**
     * Met à jour le nom de l'auteur pour tous ses commentaires.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Commentaire c SET c.auteurNom = :newNom WHERE c.auteurEmail = :email")
    int updateAuteurNom(@Param("email") String email, @Param("newNom") String newNom);
}
