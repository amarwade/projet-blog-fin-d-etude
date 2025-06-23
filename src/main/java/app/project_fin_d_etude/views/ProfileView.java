package app.project_fin_d_etude.views;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue de profil utilisateur : affiche les informations Keycloak de
 * l'utilisateur connecté.
 */
@Route(value = "user/profile", layout = MainLayout.class)
@PageTitle("Mon Profil")
public class ProfileView extends VerticalLayout {

    /**
     * Construit la vue profil et affiche les informations Keycloak de
     * l'utilisateur connecté.
     */
    public ProfileView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Background.CONTRAST_5);

        add(createMainSection());
        add(createProfileCard());
    }

    /**
     * Crée la section principale (titre, séparateurs).
     */
    private VerticalLayout createMainSection() {
        final VerticalLayout mainSection = new VerticalLayout();
        mainSection.setWidth("100%");
        mainSection.setAlignItems(Alignment.CENTER);
        mainSection.addClassNames(
                LumoUtility.Padding.Vertical.LARGE,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST
        );
        mainSection.add(VaadinUtils.createSeparator("80%"));
        mainSection.add(createMainTitle());
        mainSection.add(VaadinUtils.createSeparator("80%"));
        return mainSection;
    }

    /**
     * Crée le titre principal de la page profil.
     */
    private H1 createMainTitle() {
        final H1 title = new H1("INFORMATIONS PERSONNELLES");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontWeight.BOLD
        );
        return title;
    }

    /**
     * Crée la carte d'affichage du profil utilisateur connecté.
     */
    private VerticalLayout createProfileCard() {
        final VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.setAlignItems(Alignment.START);
        card.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.Padding.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );
        card.setMaxWidth("800px");
        card.setWidthFull();

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser oidcUser) {
            card.add(new H2("Profil Utilisateur"));
            final String nom = oidcUser.getGivenName();
            final String prenom = oidcUser.getFamilyName();
            final String email = oidcUser.getEmail();
            final String username = oidcUser.getPreferredUsername();
            card.add(new Paragraph("Nom : " + (nom != null ? nom : "Non renseigné")));
            card.add(new Paragraph("Prénom : " + (prenom != null ? prenom : "Non renseigné")));
            card.add(new Paragraph("Email : " + (email != null ? email : "Non renseigné")));
            card.add(new Paragraph("Nom d'utilisateur : " + (username != null ? username : "Non renseigné")));
        } else {
            card.add(new Paragraph("Aucune information de profil disponible. Veuillez vous reconnecter."));
        }
        return card;
    }
}
