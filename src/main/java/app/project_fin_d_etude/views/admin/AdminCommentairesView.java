package app.project_fin_d_etude.views.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.AdminLayout;
import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.presenter.CommentairePresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import org.springframework.security.access.annotation.Secured;

/**
 * Vue d'administration des commentaires : affichage automatique et gestion.
 */
@Route(value = "admin/commentaires", layout = AdminLayout.class)
@PageTitle("Gestion des commentaires - Administration")
@AnonymousAllowed
public class AdminCommentairesView extends VerticalLayout implements CommentairePresenter.CommentaireView {

    private final CommentairePresenter commentairePresenter;
    private final Grid<Commentaire> grid = new Grid<>(Commentaire.class);
    private final Paragraph noCommentsMessage = new Paragraph("Aucun commentaire à afficher.");

    @Autowired
    public AdminCommentairesView(CommentairePresenter commentairePresenter) {
        this.commentairePresenter = commentairePresenter;
        this.commentairePresenter.setView(this);

        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassName("admin-commentaires-root");

        add(createMainContent());
        configureGrid();

        // Affichage automatique des commentaires dès le chargement
        VaadinUtils.showLoading(this);
        commentairePresenter.chargerTousLesCommentaires();
    }

    /**
     * Crée le layout principal (titre, section, grid).
     */
    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("100%");
        mainContent.setPadding(true);
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.addClassName("admin-commentaires-main-content");

        mainContent.add(createPageTitle());
        mainContent.add(createContentSection());

        return mainContent;
    }

    /**
     * Crée le titre principal de la page.
     */
    private H1 createPageTitle() {
        H1 pageTitle = new H1("GESTION DES COMMENTAIRES");
        pageTitle.addClassName("main-title");
        pageTitle.addClassName("admin-commentaires-title");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        return pageTitle;
    }

    /**
     * Crée la section contenant la grille des commentaires.
     */
    private VerticalLayout createContentSection() {
        VerticalLayout contentSection = new VerticalLayout();
        contentSection.setWidth("100%");
        contentSection.setHeight("600px");
        contentSection.addClassName("admin-commentaires-content-section");

        contentSection.add(grid);
        noCommentsMessage.addClassName("admin-commentaires-empty-message");
        noCommentsMessage.setVisible(false);
        contentSection.add(noCommentsMessage);
        return contentSection;
    }

    /**
     * Configure la grille d'affichage des commentaires.
     */
    private void configureGrid() {
        grid.addClassName("admin-commentaires-grid");
        grid.setColumns("id", "dateCreation");

        // Colonne contenu multi-ligne, tronquée à 100 caractères
        grid.addComponentColumn(commentaire -> {
            String contenu = commentaire.getContenu();
            String contenuAffiche = contenu != null && contenu.length() > 100 ? contenu.substring(0, 100) + "…" : contenu;
            Span contenuSpan = new Span(contenuAffiche);
            contenuSpan.addClassName("admin-commentaires-contenu");
            return contenuSpan;
        }).setHeader("Contenu").setAutoWidth(true).setFlexGrow(1);

        // Colonne pour l'auteur (nom de l'utilisateur)
        grid.addColumn(commentaire -> {
            if (commentaire.getAuteurNom() != null) {
                return commentaire.getAuteurNom();
            }
            return "Auteur inconnu";
        }).setHeader("Auteur");

        // Colonne pour l'article (titre du post) avec style personnalisé sans méthode dépréciée
        grid.addComponentColumn(commentaire -> {
            String titre = (commentaire.getPost() != null) ? commentaire.getPost().getTitre() : "Article inconnu";
            Span titreSpan = new Span(titre);
            titreSpan.addClassName("admin-commentaires-article");
            return titreSpan;
        })
                .setHeader("Titre de l'article")
                .setWidth("50px")
                .setFlexGrow(0);

        grid.addComponentColumn(commentaire -> {
            boolean inapproprie = commentaire.isInapproprie();
            Span badge = new Span(String.valueOf(inapproprie));
            if (inapproprie) {
                badge.getStyle().set("background", "#e6f4ea")
                        .set("color", "#1b5e20")
                        .set("padding", "4px 12px")
                        .set("border-radius", "12px")
                        .set("font-weight", "bold");
            } else {
                badge.getStyle().set("background", "#ffebee")
                        .set("color", "#b71c1c")
                        .set("padding", "4px 12px")
                        .set("border-radius", "12px")
                        .set("font-weight", "bold");
            }
            return badge;
        }).setHeader("Inapproprié");

        grid.addComponentColumn(commentaire -> {
            Button actionBtn = new Button(
                    commentaire.isInapproprie() ? "Rendre approprié" : "Marquer inapproprié",
                    e -> {
                        commentaire.setInapproprie(!commentaire.isInapproprie());
                        commentairePresenter.modifier(commentaire);
                        grid.getDataProvider().refreshItem(commentaire);
                        grid.getDataProvider().refreshAll();
                    }
            );
            actionBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            return actionBtn;
        }).setHeader("Modération");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    /**
     * Affiche les commentaires dans la grille, ou un message si aucun
     * commentaire.
     */
    @Override
    public void afficherCommentaires(List<Commentaire> commentaires) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this);
            if (commentaires == null || commentaires.isEmpty()) {
                grid.setItems(List.of());
                noCommentsMessage.setVisible(true);
            } else {
                grid.setItems(commentaires);
                noCommentsMessage.setVisible(false);
            }
        }));
    }

    /**
     * Affiche un message de succès.
     */
    @Override
    public void afficherMessage(String message) {
        VaadinUtils.showSuccessNotification(message);
    }

    /**
     * Affiche un message d'erreur.
     */
    @Override
    public void afficherErreur(String erreur) {
        VaadinUtils.showErrorNotification(erreur);
    }

    /**
     * Rafraîchit la liste des commentaires (optionnel).
     */
    @Override
    public void rafraichirListe() {
        // Optionnel : recharger les commentaires si besoin
    }
}
