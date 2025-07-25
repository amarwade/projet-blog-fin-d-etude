package app.project_fin_d_etude.views.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.presenter.MessagePresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import org.springframework.security.access.annotation.Secured;

@Route(value = "admin/messages", layout = AdminLayout.class)
@PageTitle("Gestion des messages - Administration")
@AnonymousAllowed
public class AdminMessagesView extends VerticalLayout implements MessagePresenter.MessageView {

    private final MessagePresenter messagePresenter;
    private Grid<Message> grid;
    private final Paragraph noMessagesMessage = new Paragraph("Aucun message à afficher.");

    @Autowired
    public AdminMessagesView(MessagePresenter messagePresenter) {
        this.messagePresenter = messagePresenter;
        this.messagePresenter.setView(this);

        // Initialiser la grille avant de construire le layout
        configureGrid();

        // Ajouter le contenu principal à la vue
        add(createMainContent());

        // Charger les données après que l'UI est construite
        VaadinUtils.showLoading(this);
        messagePresenter.chargerMessages();
    }

    /**
     * Crée le titre principal de la page.
     */
    private H1 createPageTitle() {
        H1 pageTitle = new H1("GESTION DES MESSAGES");
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
     * Crée la section contenant la grille des messages.
     */
    private VerticalLayout createContentSection() {
        VerticalLayout contentSection = new VerticalLayout();
        contentSection.setWidth("100%");
        contentSection.setHeight("600px");
        contentSection.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );

        contentSection.add(grid);
        noMessagesMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);
        noMessagesMessage.setVisible(false);
        contentSection.add(noMessagesMessage);
        return contentSection;
    }

    private void configureGrid() {
        grid = new Grid<>(Message.class, false);
        grid.addColumn(Message::getId).setHeader("Id");
        grid.addColumn(Message::getNom).setHeader("Nom");
        grid.addColumn(Message::getEmail).setHeader("Email");
        grid.addColumn(Message::getSujet).setHeader("Sujet");
        grid.addComponentColumn(message -> {
            String contenu = message.getContenu();
            String contenuAffiche = contenu != null && contenu.length() > 100 ? contenu.substring(0, 100) + "…" : contenu;
            Span contenuSpan = new Span(contenuAffiche);
            contenuSpan.addClassName("admin-messages-contenu");
            return contenuSpan;
        }).setHeader("Contenu").setWidth("120px").setFlexGrow(0);
        grid.addColumn(Message::getDateEnvoi).setHeader("Date Envoi");
        grid.addColumn(Message::isLu).setHeader("Lu");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
    }

    /**
     * Affiche les messages dans la grille, ou un message si aucun message.
     */
    @Override
    public void afficherMessages(List<Message> messages) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this);
            if (messages == null || messages.isEmpty()) {
                grid.setItems(List.of());
                noMessagesMessage.setVisible(true);
            } else {
                grid.setItems(messages);
                noMessagesMessage.setVisible(false);
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
}
