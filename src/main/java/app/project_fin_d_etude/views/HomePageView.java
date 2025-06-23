package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.Routes;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue principale de l'application affichant la page d'accueil. Cette vue
 * présente les articles récents.
 */
@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil")
public class HomePageView extends VerticalLayout implements PostPresenter.PostView {

    private static final int MAX_ARTICLES = 6;
    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String MAIN_DESCRIPTION = "L'objectif du site est de fournir un espace clair et structuré pour le partage de connaissances, "
            + "d'opinions ou d'actualités tout en assurant la modération et la fiabilité des échanges.";

    private final DateTimeFormatter dateFormatter;
    private FlexLayout recentPostsGrid;
    private VerticalLayout loader;

    @Autowired
    public HomePageView(final PostPresenter postPresenter) {
        postPresenter.setView(this);
        this.dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        configureLayout();
        add(createMainSection());

        H3 recentPostsTitle = new H3("Articles récents");
        recentPostsTitle.getStyle().set("margin-top", "32px").set("text-align", "center").set("font-size", "1.5rem").set("font-weight", "bold").set("width", "100%");
        add(recentPostsTitle);

        recentPostsGrid = new FlexLayout();
        recentPostsGrid.setWidthFull();
        recentPostsGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        recentPostsGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        recentPostsGrid.getStyle().set("gap", "32px")
                .set("max-width", "80%")
                .set("margin", "32px auto 0 auto")
                .set("padding", "16px")
                .set("box-sizing", "border-box");
        recentPostsGrid.setVisible(false);
        add(recentPostsGrid);

        showLoader();
        postPresenter.chargerPosts();
    }

    /**
     * Configure le layout principal de la page.
     */
    private void configureLayout() {
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);
    }

    /**
     * Crée la section principale (titre, séparateurs, description).
     */
    private VerticalLayout createMainSection() {
        final VerticalLayout mainSection = VaadinUtils.createSection("100%", FlexComponent.Alignment.CENTER);
        mainSection.addClassNames(
                LumoUtility.Padding.Vertical.LARGE,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST
        );
        mainSection.add(VaadinUtils.createSeparator("80%"));
        mainSection.add(createMainTitle());
        mainSection.add(VaadinUtils.createSeparator("80%"));
        mainSection.add(createMainDescription());
        return mainSection;
    }

    /**
     * Crée le titre principal de la page.
     */
    private H1 createMainTitle() {
        final H1 title = new H1("LE BLOG");
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
     * Crée la description principale de la page.
     */
    private Paragraph createMainDescription() {
        final Paragraph description = new Paragraph(MAIN_DESCRIPTION);
        description.addClassNames(
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.FontSize.LARGE,
                "max-w-md",
                LumoUtility.Margin.AUTO
        );
        return description;
    }

    /**
     * Affiche les articles reçus (ou un message si aucun).
     */
    @Override
    public void afficherPosts(final List<Post> posts) {
        getUI().ifPresent(ui -> ui.access(() -> {
            hideLoader();
            afficherArticlesRecents(posts);
        }));
    }

    /**
     * Affiche un message de succès.
     */
    @Override
    public void afficherMessage(final String message) {
        getUI().ifPresent(ui -> ui.access(() -> VaadinUtils.showSuccessNotification(message)));
    }

    /**
     * Affiche un message d'erreur.
     */
    @Override
    public void afficherErreur(final String erreur) {
        getUI().ifPresent(ui -> ui.access(() -> VaadinUtils.showErrorNotification(erreur)));
    }

    @Override
    public void viderFormulaire() {
        // Non utilisé dans cette vue
    }

    @Override
    public void redirigerVersDetail(Long postId) {
        // Non utilisé dans cette vue
    }

    @Override
    public void afficherPost(Post post) {
        // Non utilisé dans cette vue
    }

    private void showLoader() {
        if (loader == null) {
            loader = new VerticalLayout();
            loader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            loader.setAlignItems(FlexComponent.Alignment.CENTER);
            loader.getStyle().set("padding", "50px");
            ProgressBar progress = new ProgressBar();
            progress.setIndeterminate(true);
            loader.add(new H3("Chargement des articles récents..."), progress);
        }
        add(loader);
        recentPostsGrid.setVisible(false);
    }

    private void hideLoader() {
        if (loader != null) {
            remove(loader);
        }
        recentPostsGrid.setVisible(true);
    }

    /**
     * Affiche les articles récents dans la grille, ou un message si aucun
     * article.
     */
    private void afficherArticlesRecents(final List<Post> articles) {
        recentPostsGrid.removeAll();
        if (articles == null || articles.isEmpty()) {
            final Paragraph noArticles = new Paragraph("Aucun article récent à afficher.");
            noArticles.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE);
            recentPostsGrid.add(noArticles);
            return;
        }
        articles.stream().limit(MAX_ARTICLES).forEach(post -> {
            BlogPostCard card = createPostCard(post);
            card.setWidth("320px");
            card.getStyle().set("min-width", "260px").set("max-width", "340px");
            recentPostsGrid.add(card);
        });
    }

    /**
     * Crée une carte d'article cliquable.
     */
    private BlogPostCard createPostCard(final Post post) {
        return new BlogPostCard(post, p -> getUI().ifPresent(ui -> ui.navigate(Routes.getUserArticleUrl(p.getId()))));
    }
}
