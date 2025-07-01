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

        if (isAuthenticated) {
            // Icône SVG user
            Span userIcon = new Span();
            userIcon.getElement().setProperty("innerHTML",
                    "<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' fill='currentColor' viewBox='0 0 24 24'><circle cx='12' cy='8' r='4'/><path d='M12 14c-4.418 0-8 1.79-8 4v2h16v-2c0-2.21-3.582-4-8-4z'/></svg>");
            userIcon.getStyle().set("cursor", "pointer");
            userIcon.getStyle().set("margin-left", "10px");
            userIcon.getStyle().set("margin-right", "6px");
            userIcon.getElement().setAttribute("title", "Profil utilisateur");
            userIcon.addClickListener(e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/user/profile")));
            userIcon.addClassName("header-user-icon");
            logoUserContainer.add(userIcon);
        }
        add(logoUserContainer);

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

        Button actionButton;
        if (isAuthenticated) {
            actionButton = new Button("Déconnexion", e -> getUI().ifPresent(ui -> ui.getPage().setLocation("/logout")));
            actionButton.addClassNames(
                    LumoUtility.Background.PRIMARY,
                    LumoUtility.TextColor.PRIMARY_CONTRAST,
                    LumoUtility.Padding.Horizontal.MEDIUM,
                    LumoUtility.Padding.Vertical.SMALL,
                    LumoUtility.BorderRadius.SMALL
            );
            actionButton.getStyle().set("cursor", "pointer");
            actionButton.getElement().setAttribute("title", "Se déconnecter de votre compte");
            actionButton.getElement().setAttribute("aria-label", "Se déconnecter de votre compte");
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

        HorizontalLayout buttonsContainer = new HorizontalLayout(actionButton);
        buttonsContainer.setSpacing(true);
        buttonsContainer.setAlignItems(Alignment.CENTER);

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
