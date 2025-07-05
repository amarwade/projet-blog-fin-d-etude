package app.project_fin_d_etude.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
}
