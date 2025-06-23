package app.project_fin_d_etude.presenter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.PostService;
import lombok.Setter;

/**
 * Présentateur pour la gestion des articles (posts) avec pattern MVP.
 */
@Component
public class PostPresenter {

    /**
     * -- SETTER -- Associe une vue à ce présentateur.
     */
    @Setter
    private PostView view;
    private final PostService postService;

    @Autowired
    public PostPresenter(PostService postService) {
        this.postService = postService;
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
     * Charge tous les articles, sans pagination.
     */
    public void chargerPosts() {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;
        handleAsyncOperation(
                postService.getAllPosts(),
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
                postService.getPostById(postId),
                "Erreur lors du chargement de l'article",
                optionalPost -> {
                    if (optionalPost.isPresent()) {
                        Post post = optionalPost.get();
                        currentView.afficherPost(post);
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
                    postService.savePost(post),
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
                    postService.savePost(post),
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
                postService.delete(postId),
                "Erreur lors de la suppression",
                unused -> {
                    currentView.afficherMessage("Article supprimé avec succès");
                    chargerPosts();
                }
        );
    }

    /**
     * Recherche des articles par mot-clé, sans pagination.
     */
    public void rechercherArticles(String keyword) {
        if (view == null) {
            return;
        }
        PostView currentView = this.view;
        handleAsyncOperation(
                postService.searchAllPosts(keyword),
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
     * Gère les opérations asynchrones et la gestion des erreurs.
     */
    private <T> void handleAsyncOperation(
            CompletableFuture<T> future,
            String errorMessage,
            Consumer<T> onSuccess
    ) {
        PostView currentView = this.view;
        if (currentView == null) {
            return;
        }

        UI ui = UI.getCurrent();
        if (ui == null) {
            // Log or handle the case where UI is not available
            return;
        }

        future.whenComplete((result, ex) -> {
            ui.access(() -> {
                if (ex != null) {
                    currentView.afficherErreur(errorMessage + ": " + ex.getCause().getMessage());
                } else {
                    try {
                        onSuccess.accept(result);
                    } catch (Exception e) {
                        currentView.afficherErreur("Erreur lors du traitement des données: " + e.getMessage());
                    }
                }
            });
        });
    }
}
