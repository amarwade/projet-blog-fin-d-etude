package app.project_fin_d_etude.layout;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.utils.Routes;

public class Footer extends VerticalLayout {

    public Footer() {
        setWidthFull();
        setPadding(true);
        setSpacing(true);
        addClassNames(
                LumoUtility.Background.CONTRAST_80,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Padding.Vertical.MEDIUM
        );
        addClassName("footer-sticky");

        // Liens de navigation
        HorizontalLayout footerLinks = new HorizontalLayout();
        footerLinks.addClassNames(LumoUtility.Gap.MEDIUM);
        footerLinks.add(
                createFooterLink("Accueil", Routes.HOME),
                createFooterLink("Articles", Routes.ARTICLES),
                createFooterLink("À propos", Routes.ABOUT),
                createFooterLink("Contact", Routes.CONTACT)
        );

        // Informations légales
        Div legalInfo = new Div();
        legalInfo.addClassNames(
                LumoUtility.TextColor.SECONDARY,
                LumoUtility.FontSize.SMALL,
                LumoUtility.Margin.Top.SMALL
        );
        legalInfo.setText("© 2024 Le Blog. Tous droits réservés.");

        // Liens légaux
        HorizontalLayout legalLinks = new HorizontalLayout();
        legalLinks.addClassNames(LumoUtility.Gap.SMALL, LumoUtility.Margin.Top.SMALL);
        legalLinks.add(
                createFooterLink("Mentions légales", "/mentions-legales"),
                createFooterLink("Politique de confidentialité", "/confidentialite"),
                createFooterLink("Conditions d'utilisation", "/conditions")
        );

        add(footerLinks, legalInfo, legalLinks);
    }

    private Anchor createFooterLink(String text, String href) {
        Anchor link = new Anchor(href, text);
        link.addClassNames(
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.FontSize.SMALL
        );
        link.getStyle().set("text-decoration", "none");
        link.getStyle().set("transition", "color 0.3s ease");
        return link;
    }
}
