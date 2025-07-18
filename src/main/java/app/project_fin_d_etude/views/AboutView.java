package app.project_fin_d_etude.views;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue "À propos" présentant les informations sur le site et le développeur.
 * Cette vue est accessible via la route "/about".
 */
@Route(value = "about", layout = MainLayout.class)
@PageTitle("About")
@AnonymousAllowed
public class AboutView extends VerticalLayout {

    /**
     * Constructeur de la vue À propos.
     */
    public AboutView() {
        setSpacing(false);
        setSizeFull();
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        addClassNames("about-view", LumoUtility.Background.CONTRAST_5, LumoUtility.Padding.LARGE);

        // Conteneur principal
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setWidth("100%");
        mainContainer.setMaxWidth("1200px");
        mainContainer.addClassNames("about-main-container",
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.LARGE
        );

        // Séparateur supérieur
        mainContainer.add(VaadinUtils.createSeparator("100%"));

        // Titre principal
        H3 title = VaadinUtils.createPageTitle("À PROPOS DU SITE");
        title.addClassName("about-title");
        title.setWidth("100%");
        mainContainer.add(title);

        // Séparateur sous le titre
        mainContainer.add(VaadinUtils.createSeparator("100%"));

        // Description du site
        Paragraph description = new Paragraph(
                "Ce site est une plateforme de gestion de contenu développée dans le cadre d'un projet de fin d'études. "
                + "Il permet aux utilisateurs de partager des articles, de commenter et d'interagir avec le contenu. "
                + "La plateforme est construite avec des technologies modernes et offre une expérience utilisateur intuitive."
        );
        description.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.Margin.Bottom.XLARGE,
                LumoUtility.Padding.Horizontal.LARGE
        );
        mainContainer.add(description);

        // Section Fonctionnalités
        VerticalLayout featuresSection = createSection("Fonctionnalités principales",
                "• Publication d'articles<br>"
                + "• Système de commentaires<br>"
                + "• Interface responsive<br>"
                + "• Authentification sécurisée");
        mainContainer.add(featuresSection);

        // Section Technologies
        VerticalLayout techSection = createSection("Technologies utilisées",
                "• Java Spring Boot<br>"
                + "• Vaadin Framework<br>"
                + "• PostgreSQL<br>"
                + "• Keycloak<br>"
                + "• HTML/CSS/JavaScript");
        mainContainer.add(techSection);

        add(mainContainer);
    }

    /**
     * Crée une section stylée avec un titre et un contenu HTML.
     */
    private VerticalLayout createSection(String title, String htmlContent) {
        VerticalLayout section = new VerticalLayout();
        section.setWidthFull();
        section.setSpacing(true);
        section.addClassNames(
                LumoUtility.Margin.Bottom.XLARGE,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.BorderRadius.MEDIUM
        );

        H2 sectionTitle = new H2(title);
        sectionTitle.addClassNames(
                LumoUtility.FontSize.XXLARGE,
                LumoUtility.FontWeight.BOLD,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        Paragraph content = new Paragraph();
        content.getElement().setProperty("innerHTML", htmlContent);
        content.addClassName("about-section-content");

        section.add(sectionTitle, content);
        return section;
    }
}
