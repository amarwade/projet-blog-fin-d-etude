package app.project_fin_d_etude.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;

/**
 * Vue de connexion qui redirige vers Keycloak. Cette vue est accessible aux
 * utilisateurs non connectés.
 */
@Route(value = "login", layout = MainLayout.class)
@PageTitle("Connexion")
@AnonymousAllowed
public class LoginView extends VerticalLayout {

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        addClassNames(LumoUtility.Background.CONTRAST_5, LumoUtility.Padding.LARGE);

        // Conteneur principal
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setWidth("100%");
        mainContainer.setMaxWidth("500px");
        mainContainer.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.XLARGE
        );
        mainContainer.setAlignItems(Alignment.CENTER);
        mainContainer.setSpacing(true);

        // Titre
        H1 title = new H1("Connexion");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );

        // Message d'information
        Paragraph message = new Paragraph(
                "Pour accéder à cette page, vous devez vous connecter avec votre compte Keycloak."
        );
        message.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.XLARGE
        );

        // Bouton de connexion
        Button loginButton = new Button("Se connecter avec Keycloak", e -> {
            getUI().ifPresent(ui -> ui.getPage().setLocation("/oauth2/authorization/keycloak"));
        });
        loginButton.addClassNames(
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Padding.Horizontal.XLARGE,
                LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.FontSize.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        loginButton.getStyle().set("cursor", "pointer");

        mainContainer.add(title, message, loginButton);
        add(mainContainer);
    }
}
