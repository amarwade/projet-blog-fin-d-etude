package app.project_fin_d_etude.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.PostRepository;
import app.project_fin_d_etude.utils.EntityValidator;

@Service
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * Récupère tous les posts triés par date de publication décroissante.
     */
    public List<Post> getAllPosts() {
        logger.info("Début de getAllPosts dans PostService");
        try {
            logger.info("Appel de postRepository.findAllByOrderByDatePublicationDesc()");
            List<Post> posts = postRepository.findAllByOrderByDatePublicationDesc();
            logger.info("Posts récupérés depuis le repository: {} articles", posts != null ? posts.size() : 0);
            return posts;
        } catch (Exception e) {
            logger.error("Erreur dans getAllPosts: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Récupère un post par son identifiant.
     */
    public Optional<Post> getPostById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du post ne peut pas être null");
        }
        return postRepository.findById(id);
    }

    /**
     * Recherche des posts par mot-clé avec pagination.
     */
    public Page<Post> searchPosts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        if (keyword == null || keyword.trim().isEmpty()) {
            return postRepository.findAllOrderByDatePublicationDesc(pageable);
        }
        return postRepository.searchPosts(keyword.trim(), pageable);
    }

    /**
     * Récupère une page de posts.
     */
    public List<Post> getPaginatedPosts(int page, int size) {
        validatePaginationParameters(page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> pageResult = postRepository.findAllOrderByDatePublicationDesc(pageable);
        return pageResult.getContent();
    }

    /**
     * Sauvegarde un post après validation.
     */
    public Post savePost(Post post) {
        if (post.getId() == null) {
            post.setDatePublication(java.time.LocalDateTime.now());
        }
        // Injection automatique de l'auteur connecté si non renseigné
        if (post.getAuteurNom() == null || post.getAuteurNom().isBlank() || post.getAuteurEmail() == null || post.getAuteurEmail().isBlank()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof OidcUser oidcUser) {
                if (post.getAuteurEmail() == null || post.getAuteurEmail().isBlank()) {
                    post.setAuteurEmail(oidcUser.getEmail());
                }
                String givenName = oidcUser.getGivenName();
                String familyName = oidcUser.getFamilyName();
                if (post.getAuteurNom() == null || post.getAuteurNom().isBlank()) {
                    if (givenName != null && familyName != null) {
                        post.setAuteurNom(givenName + " " + familyName);
                    } else if (oidcUser.getFullName() != null) {
                        post.setAuteurNom(oidcUser.getFullName());
                    } else {
                        post.setAuteurNom(oidcUser.getEmail());
                    }
                }
            }
        }
        validatePost(post);
        return postRepository.save(post);
    }

    /**
     * Supprime un post par son identifiant.
     */
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du post ne peut pas être null");
        }
        postRepository.deleteById(id);
    }

    /**
     * Recherche des posts par mot-clé sans pagination.
     */
    public List<Post> searchAllPosts(String keyword) {
        logger.info("[DIAG] Entrée dans PostService.searchAllPosts avec keyword='{}'", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.info("[DIAG] Keyword vide, appel de getAllPosts()");
            List<Post> all = getAllPosts();
            logger.info("[DIAG] getAllPosts() retourne {} articles", all != null ? all.size() : 0);
            return all;
        }
        List<Post> result = postRepository.searchAllPosts(keyword.trim());
        logger.info("[DIAG] Résultat de searchAllPosts (repository) : {} articles", result != null ? result.size() : 0);
        return result;
    }

    /**
     * Récupère tous les posts d'un auteur par son email.
     */
    public List<Post> getPostsByAuteurEmail(String auteurEmail) {
        if (auteurEmail == null || auteurEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("L'email de l'auteur ne peut pas être vide");
        }
        return postRepository.findAllByAuteurEmailOrderByDatePublicationDesc(auteurEmail);
    }

    /**
     * Valide les champs obligatoires d'un post.
     */
    private void validatePost(Post post) {
        EntityValidator.ValidationResult validation = EntityValidator.validatePost(post);
        if (!validation.isValid()) {
            throw new IllegalArgumentException("Post invalide: " + validation.getAllErrorsAsString());
        }
    }

    /**
     * Valide les paramètres de pagination.
     */
    private void validatePaginationParameters(int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page ne peut pas être négatif");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("La taille de page doit être positive");
        }
    }

    /**
     * Récupère un post par son identifiant et force le chargement des
     * commentaires.
     */
    @Transactional
    public Optional<Post> getPostWithCommentaires(Long id) {
        Optional<Post> postOpt = postRepository.findById(id);
        postOpt.ifPresent(post -> {
            if (post.getCommentaires() != null) {
                post.getCommentaires().size(); // Force le chargement
            }
        });
        return postOpt;
    }
}
