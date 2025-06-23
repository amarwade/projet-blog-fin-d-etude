package app.project_fin_d_etude.service;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.CommentaireRepository;
import app.project_fin_d_etude.utils.EntityValidator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;

    public CommentaireService(CommentaireRepository commentaireRepository) {
        this.commentaireRepository = commentaireRepository;
    }

    /**
     * Récupère les commentaires associés à un post de façon asynchrone.
     *
     * @param post Le post concerné
     * @return Future contenant la liste des commentaires
     */
    @Async
    public CompletableFuture<List<Commentaire>> getCommentairesByPost(Post post) {
        if (post == null) {
            throw new IllegalArgumentException("Le post ne peut pas être null");
        }
        return CompletableFuture.completedFuture(commentaireRepository.findByPost(post));
    }

    /**
     * Sauvegarde un commentaire après validation, de façon asynchrone.
     *
     * @param commentaire Le commentaire à sauvegarder
     * @return Future contenant le commentaire sauvegardé
     */
    @Async
    public CompletableFuture<Commentaire> save(Commentaire commentaire) {
        EntityValidator.ValidationResult validationResult = EntityValidator.validateCommentaire(commentaire);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Commentaire invalide: " + validationResult.getAllErrorsAsString());
        }
        return CompletableFuture.completedFuture(commentaireRepository.save(commentaire));
    }

    /**
     * Supprime un commentaire par son identifiant, de façon asynchrone.
     *
     * @param id L'identifiant du commentaire
     * @return Future complétée une fois la suppression effectuée
     */
    @Async
    public CompletableFuture<Void> delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID du commentaire ne peut pas être null");
        }
        commentaireRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Async
    public CompletableFuture<List<Commentaire>> getAllCommentaires() {
        return CompletableFuture.completedFuture(commentaireRepository.findAll());
    }
}
