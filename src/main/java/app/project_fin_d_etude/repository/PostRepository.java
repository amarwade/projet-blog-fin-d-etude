package app.project_fin_d_etude.repository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
     * Récupère un post par son id avec ses commentaires et son auteur (fetch
     * join).
     *
     * @param id Identifiant du post
     * @return Post avec commentaires et auteur
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.commentaires LEFT JOIN FETCH p.auteur WHERE p.id = :id")
    Optional<Post> findByIdWithCommentsAndAuthor(@Param("id") Long id);

    /**
     * Récupère tous les posts avec leur auteur (fetch join).
     *
     * @return Liste de posts avec auteur
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.auteur")
    List<Post> findAllWithAuteur();

    /**
     * Récupère tous les posts avec leur auteur, triés par date de publication
     * décroissante (paginé).
     *
     * @param pageable Pagination
     * @return Liste de posts avec auteur triés
     */
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.auteur ORDER BY p.datePublication DESC")
    List<Post> findAllWithAuteurPaged(Pageable pageable);

    /**
     * Récupère les posts par une liste d'ids, avec leur auteur, triés par date de publication décroissante.
     * @param ids Liste d'identifiants de posts
     * @return Liste de posts avec auteur triés*/
    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.auteur WHERE p.id IN :ids ORDER BY p.datePublication DESC")
    List<Post> findAllWithAuteurByIds(@Param("ids") List<Long> ids);

     /* Récupère tous les posts avec leur auteur de façon asynchrone.
     * @return Future contenant la liste des posts avec auteur*/
    default CompletableFuture<List<Post>> getAllPosts() {
        return CompletableFuture.supplyAsync(() -> {
            List<Post> posts = findAllWithAuteur();
            // Forcer le chargement de l'auteur pour chaque post
            for (Post post : posts) {
                if (post.getAuteur() != null) {
                    post.getAuteur().getNom();
                }
            }
            return posts;
        });
    }
}
