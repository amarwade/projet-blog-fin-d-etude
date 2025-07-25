package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import app.project_fin_d_etude.config.AppRoles;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;

// Classe de layout principal pour l'administration, accessible uniquement aux administrateurs
@Layout
@RolesAllowed(AppRoles.ADMIN) // Restreint l'accès aux utilisateurs ayant le rôle ADMIN
public class AdminLayout extends AppLayout {

    // Constructeur : initialise la structure du layout admin
    public AdminLayout() {
        setPrimarySection(Section.DRAWER); // Définit la section principale comme le menu latéral
        Div header = createHeader(); // Crée l'en-tête
        Div spacer = new Div(); // Espaceur pour l'esthétique
        spacer.getStyle().set("height", "0px");
        // Ajoute l'en-tête, l'espaceur, le menu latéral et le menu utilisateur dans un HorizontalLayout
        VerticalLayout layout = new VerticalLayout(header, spacer, createSideNav(), createUserMenu());
        layout.setWidthFull();
        layout.addClassName("admin-layout");
        layout.setAlignItems(HorizontalLayout.Alignment.START);
        addToDrawer(layout);
    }

    // Crée l'en-tête du layout avec le logo et le nom de l'application
    private Div createHeader() {

        Image logo = new Image("themes/project-fin-d-etude/logo1.png", "Logo"); // Logo personnalisé
        logo.setHeight("40px"); // Ajustez la taille si besoin

        Div header = new Div(logo);
        header.addClassNames(Display.FLEX, Padding.MEDIUM, Gap.MEDIUM, AlignItems.CENTER);
        header.getStyle().set("justify-content", "center"); // Centre le logo horizontalement
        return header;
    }

    // Crée un élément de navigation latérale (SideNavItem)
    private SideNavItem createNavItem(String label, Icon icon, String route) {
        icon.getStyle().set("margin-right", "10px"); // Espace entre l'icône et le texte
        Span text = new Span(label);
        HorizontalLayout layout = new HorizontalLayout(icon, text);
        layout.setPadding(false);
        layout.setSpacing(true);
        layout.setAlignItems(HorizontalLayout.Alignment.CENTER);
        // Retourne un SideNavItem avec le label, la route et l'icône
        return new SideNavItem(label, route, icon);
    }

    // Crée le menu latéral de navigation pour l'admin
    private SideNav createSideNav() {
        SideNav nav = new SideNav();
        nav.addClassNames(Margin.Horizontal.MEDIUM);
        nav.addClassName("admin-sidenav"); // Ajout de la classe personnalisée pour le style bleu/blanc
        // Ajoute les différents liens de navigation
        nav.addItem(createNavItem("Accueil", VaadinIcon.HOME.create(), "/"));
        nav.addItem(createNavItem("Articles", VaadinIcon.NEWSPAPER.create(), "/articles"));
        nav.addItem(createNavItem("A propos", VaadinIcon.INFO_CIRCLE.create(), "/about"));
        nav.addItem(createNavItem("Contact", VaadinIcon.PHONE.create(), "/contact"));
        nav.addItem(createNavItem("Profil", VaadinIcon.USER.create(), "/user/profile"));
        nav.addItem(createNavItem("Tableau de bord", VaadinIcon.DASHBOARD.create(), "/admin/dashboard"));
        nav.addItem(createNavItem("Gestion des articles", VaadinIcon.EDIT.create(), "/admin/articles"));
        nav.addItem(createNavItem("Gestion des utilisateurs", VaadinIcon.USERS.create(), "/admin/keycloak-users"));
        nav.addItem(createNavItem("Gestion des commentaires", VaadinIcon.COMMENT.create(), "/admin/commentaires"));
        nav.addItem(createNavItem("Listes des messages", VaadinIcon.ENVELOPE.create(), "/admin/messages"));
        return nav;
    }

    // Crée le menu utilisateur avec le bouton de déconnexion
    private Component createUserMenu() {
        Button logoutButton = new Button("Déconnexion", event -> {
            // Redirige vers /logout lors du clic
            getUI().ifPresent(ui -> ui.getPage().setLocation("/logout"));
        });
        logoutButton.addClassName("logout-btn");
        HorizontalLayout userMenu = new HorizontalLayout(logoutButton);
        userMenu.setWidthFull();
        userMenu.setJustifyContentMode(HorizontalLayout.JustifyContentMode.CENTER);
        userMenu.setAlignItems(HorizontalLayout.Alignment.CENTER);
        userMenu.getStyle().set("margin-top", "32px");
        return userMenu;
    }
}
