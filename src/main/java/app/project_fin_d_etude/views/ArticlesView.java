package app.project_fin_d_etude.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.AttachEvent;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import app.project_fin_d_etude.utils.AsyncDataLoader;

/**
 * Vue affichant la liste des articles (posts) avec pagination. Les posts sont
 * affichés automatiquement après chargement.
 */
@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Articles")
public class ArticlesView extends VerticalLayout implements PostPresenter.PostView {

    private static final String NO_ARTICLES = "Aucun article trouvé.";
    private static final String LOADING_ARTICLES = "Chargement des articles...";
    private static final String RECENT_POSTS_TITLE = "Articles récents";
    private static final String SEARCH_PLACEHOLDER = "Titre de l'article";

    private final PostPresenter postPresenter;
    private final FlexLayout gridContainer = new FlexLayout();
    private final AsyncDataLoader asyncDataLoader;
    private String currentKeyword = null;
    private VerticalLayout loader;

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
        searchBarContainer.getStyle().set("margin-top", "24px").set("margin-bottom", "32px");
        add(searchBarContainer);

        gridContainer.setWidthFull();
        gridContainer.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        gridContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        gridContainer.getStyle()
                .set("max-width", "100%")
                .set("margin", "32px auto 0 auto")
                .set("padding", "16px")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center");
        gridContainer.getStyle().set("box-sizing", "border-box");
        add(new H3(RECENT_POSTS_TITLE) {
            {
                getStyle().set("margin-top", "32px");
                getStyle().set("text-align", "center");
                getStyle().set("font-size", "1.5rem");
                getStyle().set("font-weight", "bold");
                getStyle().set("width", "100%");
            }
        });
        add(gridContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        if (attachEvent.isInitialAttach()) {
            // Utilisation du loader asynchrone
            asyncDataLoader.loadData(
                    gridContainer,
                    postPresenter::getAllPostsSync,
                    this::afficherPosts,
                    this::afficherErreur
            );
        }
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

    private H1 createMainTitle() {
        final H1 title = new H1("ARTICLES");
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
        searchField.setClearButtonVisible(true);

        Button searchButton = new Button("RECHERCHER", e -> {
            String keyword = searchField.getValue();
            asyncDataLoader.loadData(
                    gridContainer,
                    () -> postPresenter.searchAllPosts(keyword),
                    this::afficherPosts,
                    this::afficherErreur
            );
        });
        searchButton.getStyle().set("background-color", "#6c63ff").set("color", "white").set("border-radius", "8px");

        HorizontalLayout searchBar = new HorizontalLayout(searchField, searchButton);
        searchBar.setAlignItems(Alignment.CENTER);
        searchBar.setWidth(null);
        searchBar.getStyle().set("margin-bottom", "0px");
        return searchBar;
    }

    /**
     * Affiche la liste des posts dans le conteneur principal. Appelée
     * automatiquement après chargement des posts.
     */
    @Override
    public void afficherPosts(List<Post> posts) {
        getUI().ifPresent(ui -> ui.access(() -> {
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
                for (Post post : posts) {
                    BlogPostCard card = new BlogPostCard(post);
                    card.setWidth("320px");
                    card.getStyle().set("min-width", "260px").set("max-width", "340px");
                    gridContainer.add(card);
                }
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
    public void afficherErreur(String erreur) {
        gridContainer.removeAll();
        Paragraph errorMsg = new Paragraph(erreur);
        errorMsg.addClassNames(
                LumoUtility.TextColor.ERROR,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.Top.XLARGE
        );
        gridContainer.add(errorMsg);
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
