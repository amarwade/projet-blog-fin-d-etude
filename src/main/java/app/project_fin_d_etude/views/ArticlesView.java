package app.project_fin_d_etude.views;

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
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextField;
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

/**
 * Vue affichant la liste des articles (posts) avec pagination. Les posts sont
 * affichés automatiquement après chargement.
 */
@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Articles")
@AnonymousAllowed
public class ArticlesView extends VerticalLayout implements PostPresenter.PostView {

    private static final Logger logger = LoggerFactory.getLogger(ArticlesView.class);
    private static final String NO_ARTICLES = "Aucun article trouvé.";
    private static final String LOADING_ARTICLES = "Chargement des articles...";
    private static final String SEARCH_PLACEHOLDER = "Titre de l'article";

    private final PostPresenter postPresenter;
    private final FlexLayout gridContainer = new FlexLayout();
    private final AsyncDataLoader asyncDataLoader;
    private String currentKeyword = null;
    private VerticalLayout loader;
    @Autowired
    private Executor taskExecutor;

    /**
     * Constructeur de la vue Articles. Les posts sont chargés et affichés
     * automatiquement à l'initialisation.
     */
    @Autowired
    public ArticlesView(PostPresenter postPresenter, AsyncDataLoader asyncDataLoader) {
        this.postPresenter = postPresenter;
        this.asyncDataLoader = asyncDataLoader;
        this.postPresenter.setView(this);
        configureLayout();

        add(createMainSection());

        // Barre de recherche centrée dans un conteneur
        VerticalLayout searchBarContainer = new VerticalLayout(createSearchBar());
        searchBarContainer.setWidthFull();
        searchBarContainer.setAlignItems(Alignment.CENTER);
        searchBarContainer.addClassName("articles-search-bar-container");
        searchBarContainer.getStyle().remove("margin-top");
        searchBarContainer.getStyle().remove("margin-bottom");

        add(searchBarContainer);

        gridContainer.setWidthFull();
        gridContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        gridContainer.addClassName("articles-grid");
        gridContainer.getStyle().clear();
        gridContainer.getStyle()
                .set("max-width", "100%")
                .set("margin", "0px auto 0 auto")
                .set("padding", "0px")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center");
        gridContainer.getStyle().set("box-sizing", "border-box");
        add(gridContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        logger.info("onAttach appelé, initialAttach: {}", attachEvent.isInitialAttach());
        if (attachEvent.isInitialAttach()) {
            logger.info("Début du chargement des articles");
            Paragraph loadingMessage = new Paragraph(LOADING_ARTICLES);
            loadingMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE);
            gridContainer.add(loadingMessage);
            // Affichage de données fictives d'abord
            logger.info("Test avec des données fictives");
            List<Post> testPosts = createTestPosts();
            getUI().ifPresent(ui -> ui.access(() -> {
                gridContainer.removeAll();
                gridContainer.getElement().getChildren().forEach(child -> child.removeFromParent());
                afficherPosts(testPosts);
            }));
            // Chargement réel en asynchrone
            taskExecutor.execute(() -> {
                try {
                    List<Post> posts = postPresenter.getAllPostsSync();
                    logger.info("Chargement asynchrone réussi: {} posts", posts != null ? posts.size() : 0);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        gridContainer.removeAll();
                        gridContainer.getElement().getChildren().forEach(child -> child.removeFromParent());
                        afficherPosts(posts);
                    }));
                } catch (Exception e) {
                    logger.error("Erreur lors du chargement asynchrone: {}", e.getMessage(), e);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        gridContainer.removeAll();
                        gridContainer.getElement().getChildren().forEach(child -> child.removeFromParent());
                        Paragraph errorMsg = new Paragraph("Erreur lors du chargement des articles depuis la base de données. Affichage des données de test.");
                        errorMsg.getStyle().set("color", "orange").set("font-weight", "bold").set("font-size", "1.2em");
                        gridContainer.add(errorMsg);
                        afficherPosts(testPosts);
                        Button retryButton = new Button("Réessayer", event -> {
                            gridContainer.removeAll();
                            gridContainer.getElement().getChildren().forEach(child -> child.removeFromParent());
                            onAttach(attachEvent);
                        });
                        gridContainer.add(retryButton);
                    }));
                }
            });
        }
    }

    private List<Post> createTestPosts() {
        List<Post> testPosts = new ArrayList<>();
        Post testPost1 = new Post();
        testPost1.setId(1L);
        testPost1.setTitre("Article de test 1");
        testPost1.setContenu("Ceci est le contenu du premier article de test. Il contient du texte pour tester l'affichage des articles.");
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
        return mainSection;
    }

    private H3 createMainTitle() {
        final H3 title = new H3("ARTICLES");
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
     * Configure le layout principal de la vue.
     */
    private void configureLayout() {
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);
        gridContainer.setWidthFull();
    }

    /**
     * Crée la barre de recherche d'articles.
     */
    private HorizontalLayout createSearchBar() {
        TextField searchField = new TextField();
        searchField.setPlaceholder(SEARCH_PLACEHOLDER);
        searchField.setWidth("350px");
        searchField.getStyle().set("border-radius", "10px").set("padding", "8px");
        searchField.setClearButtonVisible(true);

        Button searchButton = new Button("RECHERCHER", e -> {
            String keyword = searchField.getValue();
            taskExecutor.execute(() -> {
                List<Post> result = postPresenter.searchAllPosts(keyword);
                getUI().ifPresent(ui -> ui.access(() -> {
                    afficherPosts(result);
                }));
            });
        });
        searchButton.getStyle().set("background", "#6c63ff").set("color", "white");
        searchButton.getStyle().set("margin-left", "10px");

        HorizontalLayout searchBar = new HorizontalLayout(searchField, searchButton);
        searchBar.setAlignItems(Alignment.CENTER);
        return searchBar;
    }

    /**
     * Affiche la liste des posts dans le conteneur principal. Appelée
     * automatiquement après chargement des posts.
     */
    @Override
    public void afficherPosts(List<Post> posts) {
        getUI().ifPresent(ui -> ui.access(() -> {
            // Suppression explicite de tous les overlays/loaders enfants
            gridContainer.getElement().getChildren()
                    .filter(child -> child.getClassList().contains("loading-overlay"))
                    .forEach(child -> child.removeFromParent());
            gridContainer.setVisible(true);
            gridContainer.removeAll();
            if (posts == null || posts.isEmpty()) {
                Paragraph emptyMsg = new Paragraph(NO_ARTICLES);
                emptyMsg.addClassNames(
                        LumoUtility.TextColor.SECONDARY,
                        LumoUtility.TextAlignment.CENTER,
                        LumoUtility.FontSize.LARGE,
                        LumoUtility.Margin.Top.XLARGE
                );
                gridContainer.add(emptyMsg);
            } else {
                posts.forEach(post -> {
                    BlogPostCard card = new BlogPostCard(post);
                    gridContainer.add(card);
                });
            }
        }));
    }

    private void showLoader() {
        if (loader == null) {
            loader = new VerticalLayout();
            loader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            loader.setAlignItems(Alignment.CENTER);
            loader.getStyle().set("padding", "50px");
            ProgressBar progress = new ProgressBar();
            progress.setIndeterminate(true);
            loader.add(new H3(LOADING_ARTICLES), progress);
        }
        if (loader.getParent().isEmpty()) {
            add(loader);
        }
        gridContainer.setVisible(false);
    }

    private void hideLoader() {
        if (loader != null && loader.getParent().isPresent()) {
            remove(loader);
        }
        gridContainer.setVisible(true);
    }

    @Override
    public void afficherErreur(final String erreur) {
        System.out.println("[DIAG] afficherErreur appelé avec : " + erreur);
        getUI().ifPresent(ui -> ui.access(() -> {
            gridContainer.removeAll();
            gridContainer.getElement().getChildren().forEach(child -> child.removeFromParent());
            Paragraph errorMsg = new Paragraph("Erreur : " + erreur);
            errorMsg.getStyle().set("color", "red").set("font-weight", "bold").set("font-size", "1.2em");
            gridContainer.add(errorMsg);
        }));
    }

    // ... autres méthodes d'interface non utilisées
    @Override
    public void afficherPost(Post post) {
    }

    @Override
    public void viderFormulaire() {
    }

    @Override
    public void afficherMessage(String message) {
    }

    @Override
    public void redirigerVersDetail(Long postId) {
    }
}
