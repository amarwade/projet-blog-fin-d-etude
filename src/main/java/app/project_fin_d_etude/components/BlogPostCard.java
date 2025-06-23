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
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;

public class BlogPostCard extends Div {

    // Longueur maximale de l'extrait du contenu affiché
    private static final int MAX_EXCERPT_LENGTH = 200;
    // Format d'affichage de la date de publication
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Construit une carte d'aperçu d'un article de blog.
     *
     * @param post L'article à afficher
     * @param onDetailClick Action à exécuter lors du clic sur le bouton "Voir
     * le détail"
     */
    public BlogPostCard(Post post, Consumer<Post> onDetailClick) {
        // Style général de la carte (glassmorphism, pas de fond sombre)
        addClassNames(LumoUtility.BoxShadow.MEDIUM);
        getStyle()
                .set("border-radius", "16px")
                .set("border", "1.5px solid #e0e0e0")
                .set("background", "rgba(255,255,255,0.7)")
                .set("backdrop-filter", "blur(6px)")
                .set("-webkit-backdrop-filter", "blur(6px)")
                .set("transition", "box-shadow 0.2s")
                .set("box-shadow", "0 4px 24px 0 rgba(0,0,0,0.08)")
                .set("height", "200px")
                .set("max-width", "340px")
                .set("overflow", "hidden")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("padding", "20px");
        getElement().executeJs(
                "this.addEventListener('mouseenter', () => this.style.boxShadow='0 8px 32px 0 rgba(108,99,255,0.18)');"
                + "this.addEventListener('mouseleave', () => this.style.boxShadow='0 4px 24px 0 rgba(0,0,0,0.08)');");

        // Date stylée en haut à gauche
        Span dateSpan = new Span(Optional.ofNullable(post.getDatePublication())
                .map(d -> d.format(DateTimeFormatter.ofPattern("EEEE, d MMM yyyy")))
                .orElse("Date inconnue"));
        dateSpan.getStyle()
                .set("font-size", "0.85em")
                .set("color", "#6c63ff")
                .set("font-weight", "bold")
                .set("margin-bottom", "8px");

        H3 cardTitle = createTitle(post);
        Paragraph cardDescription = createDescription(post);
        Button detailButton = createDetailButton(post, onDetailClick);
        detailButton.setText("Lire plus");
        detailButton.getStyle()
                .set("margin-left", "auto")
                .set("background-color", "#6c63ff")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("margin-bottom", "8px")
                .set("padding-left", "18px")
                .set("padding-right", "18px");

        // Layout pour contenu principal (date, titre, extrait)
        VerticalLayout contentLayout = new VerticalLayout(dateSpan, cardTitle, cardDescription);
        contentLayout.setSpacing(false);
        contentLayout.setPadding(false);
        contentLayout.setAlignItems(FlexComponent.Alignment.START);
        contentLayout.setWidthFull();
        contentLayout.getStyle().set("flex", "1 1 auto");

        // Layout pour bouton en bas
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
        H3 title = new H3(Optional.ofNullable(post.getTitre()).orElse("Titre inconnu"));
        title.addClassNames(LumoUtility.FontSize.XLARGE, LumoUtility.Margin.Bottom.SMALL, LumoUtility.FontWeight.BOLD);
        title.getStyle().set("white-space", "nowrap").set("overflow", "hidden").set("text-overflow", "ellipsis").set("max-width", "90%");
        return title;
    }

    /**
     * Crée l'extrait du contenu de l'article (tronqué si nécessaire).
     */
    private Paragraph createDescription(Post post) {
        String contenu = Optional.ofNullable(post.getContenu()).orElse("");
        String extrait = contenu.length() > MAX_EXCERPT_LENGTH ? contenu.substring(0, MAX_EXCERPT_LENGTH) + "..." : contenu;
        Paragraph description = new Paragraph(extrait);
        description.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.Bottom.SMALL);
        description.getStyle().set("overflow", "hidden").set("text-overflow", "ellipsis").set("display", "-webkit-box").set("-webkit-line-clamp", "3").set("-webkit-box-orient", "vertical").set("max-width", "95%");
        return description;
    }

    /**
     * Crée le bouton permettant d'accéder au détail de l'article.
     */
    private Button createDetailButton(Post post, Consumer<Post> onDetailClick) {
        Button button = new Button("Voir le détail", e -> onDetailClick.accept(post));
        button.addClassNames(
                LumoUtility.Margin.Top.MEDIUM,
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.XSMALL,
                LumoUtility.BorderRadius.SMALL
        );
        return button;
    }
}
