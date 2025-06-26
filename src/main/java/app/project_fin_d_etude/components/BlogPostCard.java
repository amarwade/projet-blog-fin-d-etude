package app.project_fin_d_etude.components;

import app.project_fin_d_etude.model.Post;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.UI;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BlogPostCard extends Div {

    // Constantes pour les textes et les classes CSS
    private static final int MAX_EXCERPT_LENGTH = 200;
    private static final String UNKNOWN_TITLE = "Titre inconnu";
    private static final String UNKNOWN_DATE = "Date inconnue";
    private static final String READ_MORE = "Détails";

    private static final String CARD_CLASS = "blog-post-card";
    private static final String DATE_CLASS = "blog-post-date";
    private static final String TITLE_CLASS = "blog-post-title";
    private static final String DESCRIPTION_CLASS = "blog-post-description";
    private static final String BUTTON_CLASS = "blog-post-button";

    /**
     * Construit une carte d'aperçu d'un article de blog.
     *
     * @param post L'article à afficher
     */
    public BlogPostCard(Post post) {
        addClassName(CARD_CLASS);
        getStyle()
                .set("background", "#fff")
                .set("border-radius", "20px")
                .set("box-shadow", "0 4px 16px rgba(44,62,80,0.13)")
                .set("padding", "28px 24px 24px 24px")
                .set("margin", "16px")
                .set("width", "300px")
                .set("min-width", "220px")
                .set("max-width", "320px")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "space-between")
                .set("transition", "box-shadow 0.2s, transform 0.2s")
                .set("cursor", "pointer");
        getElement().addEventListener("mouseenter", e -> getStyle().set("box-shadow", "0 8px 24px rgba(44,62,80,0.18)").set("transform", "translateY(-2px)"));
        getElement().addEventListener("mouseleave", e -> getStyle().set("box-shadow", "0 4px 16px rgba(44,62,80,0.13)").set("transform", "none"));

        Span dateSpan = new Span(Optional.ofNullable(post.getDatePublication())
                .map(d -> d.format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")))
                .orElse(UNKNOWN_DATE));
        dateSpan.addClassName(DATE_CLASS);
        dateSpan.getStyle()
                .set("color", "#4f6cfb")
                .set("font-size", "1rem")
                .set("font-style", "italic")
                .set("margin-bottom", "8px")
                .set("font-weight", "600");

        H3 cardTitle = createTitle(post);
        Paragraph cardDescription = createDescription(post);
        Button detailButton = createDetailButton(post);

        VerticalLayout contentLayout = new VerticalLayout(dateSpan, cardTitle, cardDescription);
        contentLayout.setSpacing(false);
        contentLayout.setPadding(false);
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setWidthFull();
        contentLayout.getStyle().set("flex", "1 1 auto").set("margin-bottom", "18px");

        HorizontalLayout buttonLayout = new HorizontalLayout(detailButton);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttonLayout.getStyle().set("margin-top", "auto");

        add(contentLayout, buttonLayout);
    }

    /**
     * Crée le titre de la carte à partir du titre du post.
     */
    private H3 createTitle(Post post) {
        H3 title = new H3(Optional.ofNullable(post.getTitre()).orElse(UNKNOWN_TITLE));
        title.addClassName(TITLE_CLASS);
        title.getStyle()
                .set("font-size", "1.45rem")
                .set("font-weight", "bold")
                .set("color", "#181c32")
                .set("margin", "0 0 10px 0")
                .set("white-space", "normal")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("max-width", "98%");
        return title;
    }

    /**
     * Crée l'extrait du contenu de l'article (tronqué si nécessaire).
     */
    private Paragraph createDescription(Post post) {
        String contenu = Optional.ofNullable(post.getContenu()).orElse("");
        String extrait = contenu.length() > MAX_EXCERPT_LENGTH ? contenu.substring(0, MAX_EXCERPT_LENGTH) + "..." : contenu;
        Paragraph description = new Paragraph(extrait);
        description.addClassName(DESCRIPTION_CLASS);
        description.getStyle()
                .set("color", "#3a3a3a")
                .set("font-size", "1.08rem")
                .set("margin", "0 0 0 0")
                .set("overflow", "hidden")
                .set("text-overflow", "ellipsis")
                .set("display", "-webkit-box")
                .set("-webkit-line-clamp", "3")
                .set("-webkit-box-orient", "vertical")
                .set("max-width", "98%");
        return description;
    }

    /**
     * Crée le bouton permettant d'accéder au détail de l'article.
     */
    private Button createDetailButton(Post post) {
        Button button = new Button(READ_MORE);
        button.addClickListener(e -> {
            button.setEnabled(false); // Désactive le bouton après clic
            getUI().ifPresent(ui -> ui.navigate("user/article/" + post.getId()));
        });
        button.addClassName(BUTTON_CLASS);
        button.getElement().setAttribute("aria-label", READ_MORE + " sur " + post.getTitre());
        button.getStyle()
                .set("margin-left", "auto")
                .set("background-color", "#6c63ff")
                .set("color", "white")
                .set("border-radius", "10px")
                .set("margin-bottom", "8px")
                .set("padding-left", "28px")
                .set("padding-right", "28px")
                .set("padding-top", "10px")
                .set("padding-bottom", "10px")
                .set("font-size", "1.08rem")
                .set("font-weight", "bold")
                .set("box-shadow", "0 2px 8px rgba(44,62,80,0.10)");
        return button;
    }
}
