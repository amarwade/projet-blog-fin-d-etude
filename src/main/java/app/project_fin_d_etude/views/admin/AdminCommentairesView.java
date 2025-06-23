package app.project_fin_d_etude.views.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.AdminLayout;
import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.presenter.CommentairePresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import jakarta.annotation.security.RolesAllowed;

/**
 * Vue d'administration des commentaires : affichage automatique et gestion.
 */
@Route(value = "admin/commentaires", layout = AdminLayout.class)
@PageTitle("Gestion des commentaires - Administration")
@RolesAllowed("ADMIN")
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
        addClassNames(LumoUtility.Background.CONTRAST_5);

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
        mainContent.addClassNames(
                LumoUtility.Margin.AUTO,
                LumoUtility.Background.CONTRAST_10,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );

        mainContent.add(createPageTitle());
        mainContent.add(createContentSection());

        return mainContent;
    }

    /**
     * Crée le titre principal de la page.
     */
    private H1 createPageTitle() {
        H1 pageTitle = new H1("GESTION DES COMMENTAIRES");
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
        contentSection.setWidth("90%");
        contentSection.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );

        contentSection.add(grid);
        noCommentsMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);
        noCommentsMessage.setVisible(false);
        contentSection.add(noCommentsMessage);
        return contentSection;
    }

    /**
     * Configure la grille d'affichage des commentaires.
     */
    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setColumns("id", "contenu", "dateCreation");

        // Colonne pour l'auteur (nom de l'utilisateur)
        grid.addColumn(commentaire -> {
            if (commentaire.getAuteur() != null) {
                return commentaire.getAuteur().getNom();
            }
            return "Auteur inconnu";
        }).setHeader("Auteur");

        // Colonne pour l'article (titre du post)
        grid.addColumn(commentaire -> {
            if (commentaire.getPost() != null) {
                return commentaire.getPost().getTitre();
            }
            return "Article inconnu";
        }).setHeader("Article");

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
