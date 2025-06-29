package app.project_fin_d_etude.components;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import app.project_fin_d_etude.model.Post;

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
        getElement().addEventListener("mouseenter", e -> getElement().getClassList().add("hover"));
        getElement().addEventListener("mouseleave", e -> getElement().getClassList().remove("hover"));

        Span dateSpan = new Span(Optional.ofNullable(post.getDatePublication())
                .map(d -> d.format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")))
                .orElse(UNKNOWN_DATE));
        dateSpan.addClassName(DATE_CLASS);

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
        return button;
    }
}
