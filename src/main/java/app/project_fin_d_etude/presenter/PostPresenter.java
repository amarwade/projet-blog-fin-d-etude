package app.project_fin_d_etude.presenter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.PostService;
import app.project_fin_d_etude.service.CommentaireService;

import lombok.Setter;

/**
 * Présentateur pour la gestion des articles (posts) avec le pattern MVP.
 */
@Component
public class PostPresenter {

    @Setter
    private PostView view;

    private final PostService postService;
    private final CommentaireService commentaireService;

    @Autowired
    public PostPresenter(PostService postService, CommentaireService commentaireService) {
        this.postService = postService;
        this.commentaireService = commentaireService;
    }

    /**
     * Interface à implémenter par la vue pour lier le présentateur.
     */
    public interface PostView {

        void afficherPost(Post post);

        void afficherPosts(List<Post> posts);

        void afficherMessage(String message);

        void afficherErreur(String erreur);

        void viderFormulaire();

        void redirigerVersDetail(Long postId);
    }

    /**
     * Récupère tous les posts de façon synchrone (bloquante).
     */
    public List<Post> getAllPostsSync() {
        try {
            return postService.getAllPosts();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des articles.", e);
        }
    }

    /**
     * Recherche des articles par mot-clé, de façon synchrone (bloquante).
     */
    public List<Post> searchAllPosts(String keyword) {
        try {
            return postService.searchAllPosts(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la recherche des articles.", e);
        }
    }

    /**
     * Charge tous les articles de manière asynchrone.
     */
    public void chargerPosts() {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        handleAsyncOperation(
                CompletableFuture.supplyAsync(postService::getAllPosts),
                "Erreur lors du chargement des articles",
                currentView::afficherPosts
        );
    }

    /**
     * Charge un article par son identifiant.
     */
    public void chargerPost(Long postId) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        handleAsyncOperation(
                CompletableFuture.supplyAsync(() -> postService.getPostById(postId)),
                "Erreur lors du chargement de l'article",
                optionalPost -> {
                    if (optionalPost.isPresent()) {
                        currentView.afficherPost(optionalPost.get());
                    } else {
                        currentView.afficherErreur("Article non trouvé");
                    }
                }
        );
    }

    /**
     * Publie un nouvel article après validation.
     */
    public void publierPost(Post post) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        if (validatePost(post, currentView)) {
            handleAsyncOperation(
                    CompletableFuture.supplyAsync(() -> postService.savePost(post)),
                    "Erreur lors de la publication",
                    savedPost -> {
                        currentView.afficherMessage("Article publié avec succès !");
                        currentView.viderFormulaire();
                        currentView.redirigerVersDetail(savedPost.getId());
                    }
            );
        }
    }

    /**
     * Modifie un article existant après validation.
     */
    public void modifierPost(Post post) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        if (validatePost(post, currentView)) {
            handleAsyncOperation(
                    CompletableFuture.supplyAsync(() -> postService.savePost(post)),
                    "Erreur lors de la modification",
                    updatedPost -> {
                        currentView.afficherMessage("Article modifié avec succès !");
                        currentView.redirigerVersDetail(updatedPost.getId());
                    }
            );
        }
    }

    /**
     * Supprime un article par son identifiant.
     */
    public void supprimerPost(Long postId) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        handleAsyncOperation(
                CompletableFuture.runAsync(() -> postService.delete(postId)),
                "Erreur lors de la suppression",
                unused -> {
                    currentView.afficherMessage("Article supprimé avec succès");
                    chargerPosts();
                }
        );
    }

    /**
     * Recherche des articles par mot-clé de façon asynchrone.
     */
    public void rechercherArticles(String keyword) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;

        handleAsyncOperation(
                CompletableFuture.supplyAsync(() -> postService.searchAllPosts(keyword)),
                "Erreur lors de la recherche",
                currentView::afficherPosts
        );
    }

    /**
     * Valide les champs obligatoires d'un article.
     */
    private boolean validatePost(Post post, PostView currentView) {
        if (post.getTitre() == null || post.getTitre().trim().isEmpty()) {
            currentView.afficherErreur("Le titre de l'article ne peut pas être vide");
            return false;
        }
        if (post.getContenu() == null || post.getContenu().trim().isEmpty()) {
            currentView.afficherErreur("Le contenu de l'article ne peut pas être vide");
            return false;
        }
        return true;
    }

    /**
     * Gère les opérations asynchrones avec affichage de résultats ou gestion
     * d'erreur.
     */
    private <T> void handleAsyncOperation(
            CompletableFuture<T> future,
            String errorMessage,
            Consumer<T> onSuccess
    ) {
        PostView currentView = this.view;
        UI ui = UI.getCurrent();

        if (currentView == null || ui == null) {
            return;
        }

        future.whenCompleteAsync((result, ex) -> {
            ui.access(() -> {
                if (ex != null) {
                    currentView.afficherErreur(errorMessage + " : " + ex.getMessage());
                } else {
                    try {
                        onSuccess.accept(result);
                    } catch (Exception e) {
                        currentView.afficherErreur("Erreur lors du traitement : " + e.getMessage());
                    }
                }
            });
        });
    }

    public void repondreAuCommentaire(Long postId, Long parentCommentaireId, String contenu, String auteurNom, String auteurEmail) {
        System.out.println(">>> Presenter: repondreAuCommentaire appelé");
        commentaireService.repondreAuCommentaire(postId, parentCommentaireId, contenu, auteurNom, auteurEmail);
    }

}
