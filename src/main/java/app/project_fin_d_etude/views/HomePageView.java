// ========== HomePageView.java ==========
package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.AsyncDataLoader;
import app.project_fin_d_etude.utils.VaadinUtils;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Accueil")
@AnonymousAllowed
public class HomePageView extends VerticalLayout implements PostPresenter.PostView {

    private static final Logger logger = LoggerFactory.getLogger(HomePageView.class);
    private static final int MAX_ARTICLES = 6;
    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String MAIN_DESCRIPTION = "L'objectif du site est de fournir un espace clair et structuré pour le partage de connaissances, opinions ou d'actualités tout en assurant la modération et la fiabilité des échanges.";

    private final DateTimeFormatter dateFormatter;
    private FlexLayout recentPostsGrid;
    private VerticalLayout postsContainer;
    private final PostPresenter postPresenter;
    private final AsyncDataLoader asyncDataLoader;
    @Autowired
    private Executor taskExecutor;

    @Autowired
    public HomePageView(final PostPresenter postPresenter, final AsyncDataLoader asyncDataLoader) {
        logger.info("Initialisation de HomePageView");
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
        logger.info("HomePageView initialisée avec succès");
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        logger.info("onAttach appelé, initialAttach: {}", attachEvent.isInitialAttach());
        if (attachEvent.isInitialAttach()) {
            logger.info("Début du chargement des posts");
            // Affichage immédiat d'un message de chargement
            Paragraph loadingMessage = new Paragraph("Chargement des articles en cours...");
            loadingMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE);
            postsContainer.add(loadingMessage);
            // Test avec des données fictives d'abord
            logger.info("Test avec des données fictives");
            List<Post> testPosts = createTestPosts();
            getUI().ifPresent(ui -> ui.access(() -> {
                postsContainer.removeAll();
                postsContainer.add(recentPostsGrid);
                afficherArticlesRecents(testPosts);
            }));
            // Chargement réel en asynchrone
            taskExecutor.execute(() -> {
                try {
                    List<Post> posts = postPresenter.getAllPostsSync();
                    logger.info("Chargement asynchrone réussi: {} posts", posts != null ? posts.size() : 0);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        postsContainer.removeAll();
                        postsContainer.add(recentPostsGrid);
                        afficherArticlesRecents(posts);
                    }));
                } catch (Exception e) {
                    logger.error("Erreur lors du chargement asynchrone: {}", e.getMessage(), e);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        postsContainer.removeAll();
                        Paragraph errorMsg = new Paragraph("Erreur lors du chargement des articles depuis la base de données. Affichage des données de test.");
                        errorMsg.getStyle().set("color", "orange").set("font-weight", "bold").set("font-size", "1.2em");
                        postsContainer.add(errorMsg);
                        postsContainer.add(recentPostsGrid);
                        afficherArticlesRecents(testPosts);
                        Button retryButton = new Button("Réessayer", event -> {
                            postsContainer.removeAll();
                            onAttach(attachEvent);
                        });
                        postsContainer.add(retryButton);
                    }));
                }
            });
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
        final H3 title = new H3("ACCUEIL");
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

    private List<Post> createTestPosts() {
        List<Post> testPosts = new ArrayList<>();

        Post testPost1 = new Post();
        testPost1.setId(1L);
        testPost1.setTitre("Article de test 1");
        testPost1.setContenu("Ceci est le contenu du premier article de test. Il contient du texte pour tester l'affichage des articles sur la page d'accueil.");
        testPost1.setAuteurNom("Admin Test");
        testPost1.setAuteurEmail("admin@test.com");
        testPost1.setDatePublication(java.time.LocalDateTime.now().minusDays(1));

        Post testPost2 = new Post();
        testPost2.setId(2L);
        testPost2.setTitre("Article de test 2");
        testPost2.setContenu("Ceci est le contenu du deuxième article de test. Il permet de vérifier que plusieurs articles s'affichent correctement.");
        testPost2.setAuteurNom("Utilisateur Test");
        testPost2.setAuteurEmail("user@test.com");
        testPost2.setDatePublication(java.time.LocalDateTime.now().minusHours(6));

        testPosts.add(testPost1);
        testPosts.add(testPost2);

        return testPosts;
    }
}
