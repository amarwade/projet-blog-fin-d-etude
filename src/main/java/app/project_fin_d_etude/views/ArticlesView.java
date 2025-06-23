package app.project_fin_d_etude.views;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.orderedlayout.FlexLayout;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue affichant la liste des articles (posts) avec pagination. Les posts sont
 * affichés automatiquement après chargement.
 */
@Route(value = "articles", layout = MainLayout.class)
@PageTitle("Articles")
public class ArticlesView extends VerticalLayout implements PostPresenter.PostView {

    private final PostPresenter postPresenter;
    private final FlexLayout gridContainer = new FlexLayout();
    private String currentKeyword = null;
    private VerticalLayout loader;

    /**
     * Constructeur de la vue Articles. Les posts sont chargés et affichés
     * automatiquement à l'initialisation.
     */
    @Autowired
    public ArticlesView(PostPresenter postPresenter) {
        this.postPresenter = postPresenter;
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
        gridContainer.getStyle().set("gap", "32px");
        gridContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        gridContainer.getStyle().set("max-width", "80%").set("margin", "32px auto 0 auto").set("padding", "16px");
        gridContainer.getStyle().set("box-sizing", "border-box");
        add(new H3("Recent blog posts") {
            {
                //mettre un left margin auto pour centrer le titre
                getStyle().set("margin-top", "32px");
                getStyle().set("text-align", "center");
                getStyle().set("font-size", "1.5rem");
                getStyle().set("font-weight", "bold");
                getStyle().set("width", "100%");
                

            }
        });
        add(gridContainer);

        showLoader();
        postPresenter.chargerPosts();
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
        searchField.setPlaceholder("Articles name or category");
        searchField.setWidth("350px");
        searchField.setClearButtonVisible(true);
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            String value = e.getValue();
            showLoader();
            currentKeyword = (value != null) ? value.trim() : null;
            postPresenter.rechercherArticles(currentKeyword);
        });

        Button searchButton = new Button("RECHERCHER", e -> {
            showLoader();
            postPresenter.rechercherArticles(searchField.getValue());
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
        hideLoader();
        gridContainer.removeAll();
        if (posts == null || posts.isEmpty()) {
            Paragraph emptyMsg = new Paragraph("Aucun article trouvé.");
            emptyMsg.addClassNames(
                    LumoUtility.TextColor.SECONDARY,
                    LumoUtility.TextAlignment.CENTER,
                    LumoUtility.FontSize.LARGE,
                    LumoUtility.Margin.Top.XLARGE
            );
            gridContainer.add(emptyMsg);
        } else {
            for (Post post : posts) {
                BlogPostCard card = new BlogPostCard(post, p -> getUI().ifPresent(ui -> ui.navigate("user/article/" + p.getId())));
                card.setWidth("320px");
                card.getStyle().set("min-width", "260px").set("max-width", "340px");
                gridContainer.add(card);
            }
        }
    }

    private void showLoader() {
        if (loader == null) {
            loader = new VerticalLayout();
            loader.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            loader.setAlignItems(Alignment.CENTER);
            loader.getStyle().set("padding", "50px");
            ProgressBar progress = new ProgressBar();
            progress.setIndeterminate(true);
            loader.add(new H3("Chargement des articles..."), progress);
        }
        add(loader);
        gridContainer.setVisible(false);
    }

    private void hideLoader() {
        if (loader != null) {
            remove(loader);
        }
        gridContainer.setVisible(true);
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
    public void afficherErreur(String erreur) {
    }

    @Override
    public void redirigerVersDetail(Long postId) {
    }
}
