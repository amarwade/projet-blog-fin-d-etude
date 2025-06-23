package app.project_fin_d_etude.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.PostRepository;
import app.project_fin_d_etude.utils.EntityValidator;

@Service
public class PostService {

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Récupère tous les posts avec leur auteur, de façon asynchrone.
     *
     * @return Future contenant la liste des posts
     */
    @Async
    public CompletableFuture<List<Post>> getAllPosts() {
        return CompletableFuture.completedFuture(postRepository.findAllByOrderByDatePublicationDesc());
    }

    /**
     * Récupère un post par son identifiant, avec commentaires et auteur, de
     * façon asynchrone.
     *
     * @param id Identifiant du post
     * @return Future contenant un Optional du post
     */
    @Async
    public CompletableFuture<Optional<Post>> getPostById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du post ne peut pas être null");
        }
        return CompletableFuture.completedFuture(postRepository.findByIdWithCommentsAndAuthor(id));
    }

    /**
     * Recherche des posts par mot-clé avec pagination, de façon asynchrone.
     *
     * @param keyword Mot-clé à rechercher
     * @param page Numéro de page
     * @param size Taille de page
     * @return Future contenant une page de posts
     */
    @Async
    public CompletableFuture<Page<Post>> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.trim().isEmpty()) {
            return CompletableFuture.completedFuture(postRepository.findAllOrderByDatePublicationDesc(pageable));
        }
        return CompletableFuture.completedFuture(postRepository.searchPosts(keyword, pageable));
    }

    /**
     * Récupère une page de posts avec leur auteur, de façon asynchrone.
     *
     * @param page Numéro de page
     * @param size Taille de page
     * @return Future contenant la liste des posts de la page
     */
    @Async
    public CompletableFuture<List<Post>> getPaginatedPosts(int page, int size) {
        validatePaginationParameters(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> pageResult = postRepository.findAll(pageable);
        List<Long> ids = pageResult.map(Post::getId).getContent();
        List<Post> posts = ids.isEmpty() ? List.of() : postRepository.findAllWithAuteurByIds(ids);
        return CompletableFuture.completedFuture(posts);
    }

    /**
     * Sauvegarde un post après validation, de façon asynchrone.
     *
     * @param post Le post à sauvegarder
     * @return Future contenant le post sauvegardé
     */
    @Async
    public CompletableFuture<Post> savePost(Post post) {
        if (post.getId() == null) {
            post.setDatePublication(java.time.LocalDateTime.now());
        }
        associateDefaultAuthor(post);
        validatePost(post);
        return CompletableFuture.completedFuture(postRepository.save(post));
    }

    /**
     * Supprime un post par son identifiant, de façon asynchrone.
     *
     * @param id Identifiant du post
     * @return Future complétée une fois la suppression effectuée
     */
    @Async
    public CompletableFuture<Void> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du post ne peut pas être null");
        }
        postRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Vérifie que le post a bien un auteur défini.
     *
     * @param post Le post à vérifier
     */
    private void associateDefaultAuthor(Post post) {
        if (post.getAuteur() == null) {
            throw new RuntimeException("L'auteur du post doit être défini (utilisateur connecté).");
        }
    }

    /**
     * Valide les champs obligatoires d'un post.
     *
     * @param post Le post à valider
     */
    private void validatePost(Post post) {
        EntityValidator.ValidationResult validation = EntityValidator.validatePost(post);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Post invalide: " + validation.getAllErrorsAsString());
        }
    }

    /**
     * Valide les paramètres de pagination.
     *
     * @param page Numéro de page
     * @param size Taille de page
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page ne peut pas être négatif");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("La taille de page doit être positive");
        }
    }

    @Async
    public CompletableFuture<List<Post>> searchAllPosts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPosts();
        }
        return CompletableFuture.completedFuture(postRepository.searchAllPosts(keyword.trim()));
    }
}
