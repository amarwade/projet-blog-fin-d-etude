package app.project_fin_d_etude.layout;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import app.project_fin_d_etude.utils.Routes;

public class Header extends HorizontalLayout {

    public Header() {
        setWidthFull();
        setPadding(true);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setDefaultVerticalComponentAlignment(Alignment.CENTER);
        addClassNames(
                LumoUtility.Background.CONTRAST_80,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.BoxShadow.SMALL
        );
        getStyle().set("position", "relative");
        getStyle().set("top", "0");
        addClassName("header-main");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser;

        // Création d'un conteneur horizontal pour le logo et l'icône user
        HorizontalLayout logoUserContainer = new HorizontalLayout();
        logoUserContainer.setSpacing(false);
        logoUserContainer.setPadding(false);
        logoUserContainer.setAlignItems(Alignment.CENTER);

        Image logoImg = new Image("themes/project-fin-d-etude/logo1.png", "Logo du blog");
        logoImg.addClassName("header-logo");
        logoUserContainer.add(logoImg);

        HorizontalLayout navLinks = new HorizontalLayout();
        navLinks.setSpacing(true);
        navLinks.addClassNames(LumoUtility.Gap.MEDIUM);

        createNavLink(navLinks, Routes.HOME, "Accueil");
        createNavLink(navLinks, Routes.ARTICLES, "Articles");
        if (isAuthenticated) {
            createNavLink(navLinks, Routes.USER_CREATE_POST, "Créer un Post");
        }
        createNavLink(navLinks, Routes.ABOUT, "A propos");
        createNavLink(navLinks, Routes.CONTACT, "Contact");

        Button actionButton = null;
        Avatar avatar = null;
        if (isAuthenticated) {
            OidcUser user = (OidcUser) authentication.getPrincipal();
            String displayName = user.getFullName() != null ? user.getFullName() : (user.getGivenName() != null ? user.getGivenName() : user.getEmail());
            String initial = displayName != null && !displayName.isEmpty() ? displayName.substring(0, 1).toUpperCase() : "?";
            avatar = new Avatar(initial);
            avatar.setColorIndex(2); // Couleur bleue
            avatar.getStyle().set("cursor", "pointer").set("margin-left", "10px");
            avatar.addClassName("header-user-avatar");

            ContextMenu menu = new ContextMenu(avatar);
            menu.setOpenOnClick(true);
            Icon userIcon = VaadinIcon.USER.create();
            userIcon.getStyle().set("margin-right", "8px");
            Icon logoutIcon = VaadinIcon.SIGN_OUT.create();
            logoutIcon.getStyle().set("margin-right", "8px");
            menu.addItem(new HorizontalLayout(userIcon, new Span("Profil")), e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/user/profile")));
            menu.addItem(new HorizontalLayout(logoutIcon, new Span("Déconnexion")), e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));
        } else {
            actionButton = new Button("Connexion", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/keycloak")));
            actionButton.addClassNames(
                    LumoUtility.Background.PRIMARY,
                    LumoUtility.TextColor.PRIMARY_CONTRAST,
                    LumoUtility.Padding.Horizontal.MEDIUM,
                    LumoUtility.Padding.Vertical.SMALL,
                    LumoUtility.BorderRadius.SMALL
            );
            actionButton.getStyle().set("cursor", "pointer");
            actionButton.getElement().setAttribute("title", "Se connecter à votre compte");
            actionButton.getElement().setAttribute("aria-label", "Se connecter à votre compte");
        }

        HorizontalLayout buttonsContainer = new HorizontalLayout();
        buttonsContainer.setSpacing(true);
        buttonsContainer.setAlignItems(Alignment.CENTER);
        if (actionButton != null) {
            buttonsContainer.add(actionButton);
        }
        if (avatar != null) {
            buttonsContainer.add(avatar);
        }
        add(logoUserContainer);
        add(navLinks, buttonsContainer);

    }

    private void createNavLink(HorizontalLayout container, String route, String text) {
        Anchor link = new Anchor(route, text);
        link.addClassNames(
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontWeight.MEDIUM
        );
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("transition", "color 0.3s ease");
        link.getStyle().set("cursor", "pointer");
        link.getElement().setAttribute("aria-label", text + " (lien de navigation)");
        container.add(link);
    }
}
