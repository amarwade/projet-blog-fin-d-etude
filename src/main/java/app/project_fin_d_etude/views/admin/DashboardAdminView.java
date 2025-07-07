package app.project_fin_d_etude.views.admin;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import app.project_fin_d_etude.layout.AdminLayout;
import app.project_fin_d_etude.service.KeycloakUserAdminService;
import app.project_fin_d_etude.service.PostService;
import app.project_fin_d_etude.service.CommentaireService;
import app.project_fin_d_etude.service.MessageService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.beans.factory.annotation.Autowired;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.security.access.annotation.Secured;

@Route(value = "admin/dashboard", layout = AdminLayout.class)
@PageTitle("Tableau de bord administrateur")
@Secured("OIDC_ADMIN")
public class DashboardAdminView extends VerticalLayout {

    @Autowired
    public DashboardAdminView(KeycloakUserAdminService userService, PostService postService, CommentaireService commentaireService, MessageService messageService) {
        setSizeFull();
        setPadding(true);
        setSpacing(true);
        setAlignItems(Alignment.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5);

        H1 title = new H1("Tableau de bord administrateur");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        add(title);

        // Section statistiques
        Div statsContainer = new Div();
        statsContainer.getStyle().set("display", "flex").set("gap", "32px").set("margin-top", "32px");

        // Utilisateurs
        int userCount = userService.listAllUsers().join().size();
        statsContainer.add(createStatCard("Utilisateurs", userCount, "#1976d2"));
        // Articles
        int postCount = postService.getAllPosts().size();
        statsContainer.add(createStatCard("Articles", postCount, "#388e3c"));
        // Commentaires
        int commentaireCount = commentaireService.getAllCommentaires().join().size();
        statsContainer.add(createStatCard("Commentaires", commentaireCount, "#fbc02d"));
        // Messages
        int messageCount = messageService.getAllMessages().join().size();
        statsContainer.add(createStatCard("Messages", messageCount, "#d32f2f"));

        add(statsContainer);
        // Ici tu pourras ajouter des widgets/statistiques plus tard

        // Section raccourcis
        Div shortcutsContainer = new Div();
        shortcutsContainer.getStyle().set("display", "flex").set("gap", "32px").set("margin-top", "48px");

        shortcutsContainer.add(createShortcut("Utilisateurs", VaadinIcon.USERS, "#1976d2", "admin/keycloak-users"));
        shortcutsContainer.add(createShortcut("Articles", VaadinIcon.FILE_TEXT, "#388e3c", "admin/articles"));
        shortcutsContainer.add(createShortcut("Commentaires", VaadinIcon.COMMENT, "#fbc02d", "admin/commentaires"));
        shortcutsContainer.add(createShortcut("Messages", VaadinIcon.ENVELOPE, "#d32f2f", "admin/messages"));

        add(shortcutsContainer);
    }

    private Div createStatCard(String label, int value, String color) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#fff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)")
                .set("padding", "24px 32px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("min-width", "140px");
        Span valueSpan = new Span(String.valueOf(value));
        valueSpan.getStyle().set("font-size", "2.5em").set("font-weight", "bold").set("color", color);
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "1.1em").set("color", "#888").set("margin-top", "8px");
        card.add(valueSpan, labelSpan);
        return card;
    }

    private Div createShortcut(String label, VaadinIcon icon, String color, String route) {
        Div card = new Div();
        card.getStyle()
                .set("background", "#fff")
                .set("border-radius", "12px")
                .set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)")
                .set("padding", "20px 32px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("min-width", "140px")
                .set("cursor", "pointer")
                .set("transition", "box-shadow 0.2s, border-color 0.2s")
                .set("border", "2px solid transparent");
        Icon ic = icon.create();
        ic.setColor(color);
        ic.setSize("32px");
        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("font-size", "1.1em").set("color", color).set("margin-top", "8px").set("font-weight", "bold");
        card.add(ic, labelSpan);
        card.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(route)));
        card.getElement().addEventListener("mouseenter", ev -> card.getStyle().set("box-shadow", "0 4px 16px rgba(64,136,207,0.12)").set("border-color", color));
        card.getElement().addEventListener("mouseleave", ev -> card.getStyle().set("box-shadow", "0 2px 8px rgba(0,0,0,0.06)").set("border-color", "transparent"));
        return card;
    }
}
