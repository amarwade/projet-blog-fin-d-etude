package app.project_fin_d_etude.views.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.AdminLayout;
import jakarta.annotation.security.RolesAllowed;

/**
 * Vue Dashboard Admin : navigation rapide et activité récente.
 */
@Route(value = "admin/dashboard", layout = AdminLayout.class)
@PageTitle("Dashboard Admin")
@RolesAllowed("ADMIN")
public class AdminDashboardView extends VerticalLayout {

    private static final String NO_ACTIVITY_TEXT = "Aucune activité récente";
    private static final String ACTIVITY_TITLE = "Activité Récente";

    /**
     * Construit la vue dashboard admin avec navigation et activité.
     */
    public AdminDashboardView() {
        setSpacing(true);
        setPadding(true);
        setWidthFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);

        add(createMainNav());
        add(createRecentActivitySection());
    }

    /**
     * Crée la barre de navigation principale admin.
     */
    private HorizontalLayout createMainNav() {
        final HorizontalLayout mainNav = new HorizontalLayout();
        mainNav.setSpacing(true);
        mainNav.setWidthFull();
        mainNav.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        mainNav.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE,
                LumoUtility.BoxShadow.SMALL
        );

        mainNav.add(
                createNavLink("Utilisateurs", AdminKeycloakUsersView.class, VaadinIcon.USERS),
                createNavLink("Posts", AdminPostsView.class, VaadinIcon.NEWSPAPER),
                createNavLink("Messages", AdminMessagesView.class, VaadinIcon.MAILBOX),
                createNavLink("Commentaires", AdminCommentairesView.class, VaadinIcon.COMMENT)
        );
        return mainNav;
    }

    /**
     * Crée la section d'activité récente.
     */
    private VerticalLayout createRecentActivitySection() {
        final VerticalLayout recentActivity = new VerticalLayout();
        recentActivity.setPadding(true);
        recentActivity.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Margin.Top.LARGE
        );

        final H2 activityTitle = new H2(ACTIVITY_TITLE);
        activityTitle.addClassNames(
                LumoUtility.Margin.Top.NONE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontSize.XLARGE
        );
        recentActivity.add(activityTitle);

        final Span noActivity = new Span(NO_ACTIVITY_TEXT);
        noActivity.addClassNames(LumoUtility.TextColor.SECONDARY);
        noActivity.getStyle().set("font-style", "italic");
        recentActivity.add(noActivity);
        return recentActivity;
    }

    /**
     * Crée un lien de navigation stylisé avec icône.
     */
    private RouterLink createNavLink(String text, Class<? extends Component> navigationTarget, VaadinIcon icon) {
        RouterLink link = new RouterLink(navigationTarget);
        link.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("gap", "0.5rem")
                .set("padding", "0.5rem 1rem")
                .set("border-radius", "var(--lumo-border-radius)")
                .set("text-decoration", "none")
                .set("color", "black");

        Icon iconComponent = icon.create();
        iconComponent.getStyle()
                .set("color", "var(--lumo-primary-color)")
                .set("background-color", "var(--lumo-shade-10pct)")
                .set("border-radius", "50%")
                .set("padding", "0.25rem");

        Span textSpan = new Span(text);
        link.add(iconComponent, textSpan);
        return link;
    }

}
