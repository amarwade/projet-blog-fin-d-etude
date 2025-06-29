package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.utils.Routes;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

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

        if (isAuthenticated) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();
            String givenName = oidcUser.getGivenName();
            String familyName = oidcUser.getFamilyName();

            String displayName = ((givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "")).trim();
            if (displayName.isBlank()) {
                displayName = oidcUser.getPreferredUsername();
            }
            if (displayName.isBlank()) {
                displayName = email;
            }

            Anchor profileLink = new Anchor("/user/profile", displayName);
            profileLink.addClassNames(
                    LumoUtility.FontSize.XLARGE,
                    LumoUtility.Margin.NONE,
                    LumoUtility.TextColor.PRIMARY,
                    LumoUtility.FontWeight.BOLD
            );
            profileLink.getStyle().set("text-decoration", "none");
            profileLink.getStyle().set("transition", "color 0.3s ease");
            profileLink.getStyle().set("cursor", "pointer");
            profileLink.getElement().setAttribute("aria-label", "Profil utilisateur : " + displayName);
            add(profileLink);
        } else {
            H3 logo = new H3("BIENVENUE!");
            logo.addClassNames(
                    LumoUtility.FontSize.XLARGE,
                    LumoUtility.Margin.NONE,
                    LumoUtility.FontWeight.BOLD
            );
            add(logo);
        }

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
