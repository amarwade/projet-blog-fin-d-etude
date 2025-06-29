// ========== HomePageView.java ==========
package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.AsyncDataLoader;
import app.project_fin_d_etude.utils.VaadinUtils;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil")
public class HomePageView extends VerticalLayout implements PostPresenter.PostView {

    private static final int MAX_ARTICLES = 6;
    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String MAIN_DESCRIPTION = "L'objectif du site est de fournir un espace clair et structuré pour le partage de connaissances, opinions ou d'actualités tout en assurant la modération et la fiabilité des échanges.";

    private final DateTimeFormatter dateFormatter;
    private FlexLayout recentPostsGrid;
    private VerticalLayout postsContainer;
    private final PostPresenter postPresenter;
    private final AsyncDataLoader asyncDataLoader;

    @Autowired
    public HomePageView(final PostPresenter postPresenter, final AsyncDataLoader asyncDataLoader) {
        this.postPresenter = postPresenter;
        this.asyncDataLoader = asyncDataLoader;
        this.postPresenter.setView(this);
        this.dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

        configureLayout();
        add(createMainSection());

        H3 recentPostsTitle = new H3("Articles récents");
        recentPostsTitle.addClassName("home-recent-posts-title");
        add(recentPostsTitle);

        postsContainer = new VerticalLayout();
        postsContainer.setPadding(false);
        postsContainer.setSpacing(false);
        postsContainer.setSizeFull();
        postsContainer.setAlignItems(Alignment.CENTER);
        add(postsContainer);

        setupRecentPostsGrid();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            asyncDataLoader.loadData(
                    postsContainer,
                    postPresenter::getAllPostsSync,
                    posts -> {
                        postsContainer.add(recentPostsGrid);
                        afficherArticlesRecents(posts);
                    },
                    errorMessage -> VaadinUtils.showErrorNotification(errorMessage),
                    attachEvent.getUI()
            );
        }
    }

    private void setupRecentPostsGrid() {
        recentPostsGrid = new FlexLayout();
        recentPostsGrid.setWidthFull();
        recentPostsGrid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        recentPostsGrid.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        recentPostsGrid.addClassName("home-recent-posts-grid");
        recentPostsGrid.getStyle().clear();
    }

    private void configureLayout() {
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);
    }

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

    private H3 createMainTitle() {
        final H3 title = new H3("LE BLOG");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontWeight.BOLD
        );
        return title;
    }

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

    @Override
    public void afficherPosts(final List<Post> posts) {
        getUI().ifPresent(ui -> ui.access(() -> {
            postsContainer.removeAll();
            postsContainer.add(recentPostsGrid);
            afficherArticlesRecents(posts);
        }));
    }

    @Override
    public void afficherMessage(final String message) {
        getUI().ifPresent(ui -> ui.access(() -> VaadinUtils.showSuccessNotification(message)));
    }

    @Override
    public void afficherErreur(final String erreur) {
        getUI().ifPresent(ui -> ui.access(() -> {
            postsContainer.removeAll();
            Paragraph errorMsg = new Paragraph("Erreur : " + erreur);
            errorMsg.getStyle().set("color", "red").set("font-weight", "bold").set("font-size", "1.2em");
            postsContainer.add(errorMsg);
        }));
    }

    @Override
    public void viderFormulaire() {
    }

    @Override
    public void redirigerVersDetail(Long postId) {
    }

    @Override
    public void afficherPost(Post post) {
    }

    private void afficherArticlesRecents(final List<Post> articles) {
        recentPostsGrid.removeAll();
        if (articles == null || articles.isEmpty()) {
            final Paragraph noArticles = new Paragraph("Aucun article récent à afficher.");
            noArticles.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE);
            recentPostsGrid.add(noArticles);
            return;
        }
        articles.stream()
                .limit(MAX_ARTICLES)
                .map(this::createPostCard)
                .forEach(recentPostsGrid::add);
    }

    private BlogPostCard createPostCard(final Post post) {
        return new BlogPostCard(post);
    }
}
