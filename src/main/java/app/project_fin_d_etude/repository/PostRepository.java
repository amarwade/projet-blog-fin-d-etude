package app.project_fin_d_etude.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import app.project_fin_d_etude.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * Recherche les posts dont le titre ou le contenu contient le mot-clé
     * (insensible à la casse).
     *
     * @param keyword Mot-clé à rechercher
     * @param pageable Pagination
     * @return Page de posts correspondants
     */
    @Query("SELECT p FROM Post p WHERE LOWER(p.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.contenu) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE LOWER(p.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) "
            + "OR LOWER(p.contenu) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY p.datePublication DESC")
    List<Post> searchAllPosts(@Param("keyword") String keyword);

    /**
     * Récupère tous les posts triés par date de publication décroissante
     * (paginé).
     *
     * @param pageable Pagination
     * @return Page de posts triés
     */
    @Query("SELECT p FROM Post p ORDER BY p.datePublication DESC")
    Page<Post> findAllOrderByDatePublicationDesc(Pageable pageable);

    List<Post> findAllByOrderByDatePublicationDesc();

    /**
     * Récupère tous les posts d'un auteur par son email.
     *
     * @param auteurEmail Email de l'auteur
     * @return Liste des posts de cet auteur
     */
    List<Post> findAllByAuteurEmailOrderByDatePublicationDesc(String auteurEmail);

    /**
     * Récupère les posts par une liste d'ids, triés par date de publication
     * décroissante.
     */
    List<Post> findByIdInOrderByDatePublicationDesc(List<Long> ids);

    /**
     * Récupère tous les posts de façon asynchrone.
     */
    default CompletableFuture<List<Post>> getAllPosts() {
        return CompletableFuture.supplyAsync(this::findAllByOrderByDatePublicationDesc);
    }

    /**
     * Met à jour l'email de l'auteur pour tous ses posts.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.auteurEmail = :newEmail WHERE p.auteurEmail = :oldEmail")
    int updateAuteurEmail(@Param("oldEmail") String oldEmail, @Param("newEmail") String newEmail);

    /**
     * Met à jour le nom de l'auteur pour tous ses posts.
     */
    @Transactional
    @Modifying
    @Query("UPDATE Post p SET p.auteurNom = :newNom WHERE p.auteurEmail = :email")
    int updateAuteurNom(@Param("email") String email, @Param("newNom") String newNom);
}
