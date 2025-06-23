package app.project_fin_d_etude.presenter;

import java.util.List;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.CommentaireService;
import lombok.Setter;

@Component
public class CommentairePresenter {

    @Setter
    private CommentaireView view;
    private final CommentaireService commentaireService;

    /**
     * Interface à implémenter par la vue pour lier le présentateur.
     */
    public interface CommentaireView {

        void afficherCommentaires(List<Commentaire> commentaires);

        void afficherMessage(String message);

        void afficherErreur(String erreur);

        void rafraichirListe();
    }

    public CommentairePresenter(CommentaireService commentaireService) {
        this.commentaireService = commentaireService;
    }

    /**
     * Charge les commentaires d'un post de façon asynchrone et met à jour la
     * vue.
     */
    public void chargerCommentaires(Post post) {
        if (view == null) {
            return;
        }
        final CommentaireView currentView = this.view;
        final UI ui = UI.getCurrent();

        commentaireService.getCommentairesByPost(post)
                .whenComplete((commentaires, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors du chargement des commentaires : " + ex.getMessage());
                        } else {
                            currentView.afficherCommentaires(commentaires);
                        }
                    });
                });
    }

    /**
     * Ajoute un commentaire après validation du contenu.
     */
    public void ajouter(Commentaire commentaire) {
        if (view == null) {
            return;
        }
        if (commentaire.getContenu() == null || commentaire.getContenu().trim().isEmpty()) {
            view.afficherErreur("Le contenu du commentaire ne peut pas être vide");
            return;
        }
        final CommentaireView currentView = this.view;
        final UI ui = UI.getCurrent();

        commentaireService.save(commentaire)
                .whenComplete((savedCommentaire, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors de l'ajout du commentaire : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Commentaire ajouté avec succès");
                            currentView.rafraichirListe();
                        }
                    });
                });
    }

    /*
     * get all commentaires
     */
    /**
     * Supprime un commentaire par son identifiant.
     */
    public void supprimer(Commentaire commentaire) {
        if (view == null) {
            return;
        }
        final CommentaireView currentView = this.view;
        final UI ui = UI.getCurrent();

        commentaireService.delete(commentaire.getId())
                .whenComplete((unused, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors de la suppression du commentaire : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Commentaire supprimé avec succès");
                            currentView.rafraichirListe();
                        }
                    });
                });
    }

    /**
     * Modifie un commentaire après validation du contenu.
     */
    public void modifier(Commentaire commentaire) {
        if (view == null) {
            return;
        }
        if (commentaire.getContenu() == null || commentaire.getContenu().trim().isEmpty()) {
            view.afficherErreur("Le contenu du commentaire ne peut pas être vide");
            return;
        }
        final CommentaireView currentView = this.view;
        final UI ui = UI.getCurrent();

        commentaireService.save(commentaire)
                .whenComplete((savedCommentaire, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors de la modification du commentaire : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Commentaire modifié avec succès");
                            currentView.rafraichirListe();
                        }
                    });
                });
    }

    public void chargerTousLesCommentaires() {
        if (view == null) {
            return;
        }
        final CommentaireView currentView = this.view;
        final UI ui = UI.getCurrent();

        commentaireService.getAllCommentaires()
                .whenComplete((commentaires, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors du chargement des commentaires : " + ex.getMessage());
                        } else {
                            currentView.afficherCommentaires(commentaires);
                        }
                    });
                });
    }
}
