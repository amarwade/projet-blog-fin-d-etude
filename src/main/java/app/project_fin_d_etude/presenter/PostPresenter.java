package app.project_fin_d_etude.presenter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.CommentaireService;
import app.project_fin_d_etude.service.PostService;
import lombok.Setter;

/**
 * Présentateur pour la gestion des articles (posts) avec le pattern MVP.
 */
@Component
public class PostPresenter {

    private static final Logger logger = LoggerFactory.getLogger(PostPresenter.class);

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
        logger.info("Début de getAllPostsSync");
        try {
            logger.info("Appel de postService.getAllPosts()");
            List<Post> posts = postService.getAllPosts();
            logger.info("Posts récupérés avec succès: {} articles", posts != null ? posts.size() : 0);
            return posts;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des articles: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération des articles.", e);
        }
    }

    /**
     * Recherche des articles par mot-clé, de façon synchrone (bloquante).
     */
    public List<Post> searchAllPosts(String keyword) {
        logger.info("[DIAG] Entrée dans PostPresenter.searchAllPosts avec keyword='{}'", keyword);
        try {
            List<Post> result = postService.searchAllPosts(keyword);
            logger.info("[DIAG] Résultat de PostPresenter.searchAllPosts : {} articles trouvés", result != null ? result.size() : 0);
            return result;
        } catch (Exception e) {
            logger.error("[DIAG] Exception dans PostPresenter.searchAllPosts : {}", e.getMessage(), e);
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
        logger.info("[DIAG] Appel de chargerPost avec postId={}", postId);
        if (view == null) {
            logger.warn("[DIAG] La vue est nulle dans chargerPost");
            return;
        }
        PostView currentView = this.view;
        logger.info("[DIAG] Classe de la vue courante: {}", currentView.getClass().getName());
        logger.info("[DIAG] UI courante dans chargerPost: {}", UI.getCurrent());

        handleAsyncOperation(
                CompletableFuture.supplyAsync(() -> {
                    logger.info("[DIAG] Appel de postService.getPostById({})", postId);
                    var opt = postService.getPostById(postId);
                    logger.info("[DIAG] Résultat de getPostById: {}", opt.isPresent() ? "trouvé" : "non trouvé");
                    return opt;
                }),
                "Erreur lors du chargement de l'article",
                optionalPost -> {
                    logger.info("[DIAG] Callback de handleAsyncOperation pour chargerPost, optionalPost présent ? {}", optionalPost.isPresent());
                    logger.info("[DIAG] Thread courant: {}", Thread.currentThread().getName());
                    logger.info("[DIAG] UI.getCurrent() dans callback: {}", UI.getCurrent());
                    try {
                        logger.info("[DIAG] Avant currentView.afficherPost");
                        if (optionalPost.isPresent()) {
                            currentView.afficherPost(optionalPost.get());
                        } else {
                            currentView.afficherErreur("Article non trouvé");
                        }
                        logger.info("[DIAG] Après currentView.afficherPost");
                    } catch (Exception e) {
                        logger.error("[DIAG] Exception dans callback chargerPost : {}", e.getMessage(), e);
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
