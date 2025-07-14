package app.project_fin_d_etude.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.CommentaireRepository;
import app.project_fin_d_etude.repository.PostRepository;
import app.project_fin_d_etude.utils.EntityValidator;

@Service
public class CommentaireService {

    private static final Logger logger = LoggerFactory.getLogger(CommentaireService.class);

    private final CommentaireRepository commentaireRepository;
    private final PostRepository postRepository;

    public CommentaireService(CommentaireRepository commentaireRepository, PostRepository postRepository) {
        this.commentaireRepository = commentaireRepository;
        this.postRepository = postRepository;
    }

    /**
     * Récupère les commentaires associés à un post de façon asynchrone.
     */
    @Async
    public CompletableFuture<List<Commentaire>> getCommentairesByPost(Post post) {
        if (post == null) {
            logger.error("Tentative de récupération des commentaires avec un post null");
            return CompletableFuture.failedFuture(new IllegalArgumentException("Le post ne peut pas être null"));
        }

        try {
            List<Commentaire> commentaires = commentaireRepository.findByPost(post);
            logger.debug("Récupération asynchrone de {} commentaires pour le post {}", commentaires.size(), post.getId());
            return CompletableFuture.completedFuture(commentaires);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération asynchrone des commentaires pour le post {}: {}", post.getId(), e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Sauvegarde un commentaire après validation, de façon synchrone.
     */
    public Commentaire save(Commentaire commentaire) {
        EntityValidator.ValidationResult validationResult = EntityValidator.validateCommentaire(commentaire);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Commentaire invalide: " + validationResult.getAllErrorsAsString());
        }
        return commentaireRepository.save(commentaire);
    }

    /**
     * Supprime un commentaire par son identifiant, de façon synchrone. Seuls
     * l'auteur du commentaire ou un administrateur peuvent supprimer un
     * commentaire.
     */
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut pas être null");
        }

        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Commentaire non trouvé avec l'ID: " + id));

        // Vérification des droits de suppression
        org.springframework.security.core.Authentication authentication
                = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
            String userEmail = oidcUser.getEmail();
            boolean isAuthor = userEmail != null && userEmail.equals(commentaire.getAuteurEmail());
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            if (!isAuthor && !isAdmin) {
                logger.warn("Tentative de suppression non autorisée du commentaire {} par l'utilisateur {}", id, userEmail);
                throw new SecurityException("Vous n'avez pas les droits pour supprimer ce commentaire");
            }
        } else {
            logger.warn("Tentative de suppression du commentaire {} par un utilisateur non authentifié", id);
            throw new SecurityException("Authentification requise pour supprimer un commentaire");
        }

        logger.info("Suppression du commentaire {} par l'utilisateur {}", id,
                authentication.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser user ? user.getEmail() : "unknown");
        commentaireRepository.deleteById(id);
    }

    @Async
    public CompletableFuture<List<Commentaire>> getAllCommentaires() {
        try {
            List<Commentaire> commentaires = commentaireRepository.findAll();
            logger.debug("Récupération asynchrone de {} commentaires", commentaires.size());
            return CompletableFuture.completedFuture(commentaires);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération asynchrone de tous les commentaires: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public Commentaire repondreAuCommentaire(Long postId, Long parentCommentaireId, String contenu, String auteurNom, String auteurEmail) {
        logger.info("Création d'une réponse au commentaire - postId: {}, parentId: {}, auteur: {}", postId, parentCommentaireId, auteurNom);
        Post post = postRepository.findById(postId).orElseThrow();
        Commentaire parent = commentaireRepository.findById(parentCommentaireId).orElseThrow();

        Commentaire reponse = new Commentaire();
        reponse.setPost(post);
        reponse.setParent(parent);
        reponse.setContenu(contenu);
        reponse.setAuteurNom(auteurNom);
        reponse.setAuteurEmail(auteurEmail);
        reponse.setDateCreation(LocalDateTime.now());

        Commentaire savedReponse = commentaireRepository.save(reponse);
        logger.info("Réponse sauvegardée avec succès - id: {}, auteur: {}", savedReponse.getId(), savedReponse.getAuteurNom());
        return savedReponse;
    }

    /**
     * Met à jour l'email de l'auteur pour tous ses commentaires.
     */
    public int migrerEmailAuteur(String ancienEmail, String nouvelEmail) {
        if (ancienEmail == null || nouvelEmail == null || ancienEmail.equals(nouvelEmail)) {
            return 0;
        }
        logger.info("Migration des commentaires de {} vers {}", ancienEmail, nouvelEmail);
        return commentaireRepository.updateAuteurEmail(ancienEmail, nouvelEmail);
    }

    /**
     * Met à jour le nom de l'auteur pour tous ses commentaires.
     */
    public int migrerNomAuteur(String email, String nouveauNom) {
        if (email == null || nouveauNom == null || email.isBlank() || nouveauNom.isBlank()) {
            return 0;
        }
        logger.info("Migration du nom d'auteur des commentaires pour {} vers {}", email, nouveauNom);
        return commentaireRepository.updateAuteurNom(email, nouveauNom);
    }
}
