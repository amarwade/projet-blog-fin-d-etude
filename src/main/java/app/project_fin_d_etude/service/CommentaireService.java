package app.project_fin_d_etude.service;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.CommentaireRepository;
import app.project_fin_d_etude.repository.PostRepository;
import app.project_fin_d_etude.utils.EntityValidator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final PostRepository postRepository;

    public CommentaireService(CommentaireRepository commentaireRepository, PostRepository postRepository) {
        this.commentaireRepository = commentaireRepository;
        this.postRepository = postRepository;
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

    public Commentaire repondreAuCommentaire(Long postId, Long parentCommentaireId, String contenu, String auteurNom, String auteurEmail) {
        System.out.println(">>> Appel repondreAuCommentaire : postId=" + postId + ", parentId=" + parentCommentaireId + ", auteurNom=" + auteurNom + ", auteurEmail=" + auteurEmail);
        Post post = postRepository.findById(postId).orElseThrow();
        Commentaire parent = commentaireRepository.findById(parentCommentaireId).orElseThrow();

        Commentaire reponse = new Commentaire();
        reponse.setPost(post);
        reponse.setParent(parent);
        reponse.setContenu(contenu);
        reponse.setAuteurNom(auteurNom);
        reponse.setAuteurEmail(auteurEmail);
        reponse.setDateCreation(LocalDateTime.now());

        System.out.println(">>> Sauvegarde réponse : id=" + reponse.getId() + ", contenu=" + reponse.getContenu() + ", auteur=" + reponse.getAuteurNom());
        return commentaireRepository.save(reponse);
    }
}
