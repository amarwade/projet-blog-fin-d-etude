package app.project_fin_d_etude.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.SecurityUtils;
import app.project_fin_d_etude.utils.VaadinUtils;
import app.project_fin_d_etude.utils.ValidationUtils;

import java.util.List;

/**
 * Vue permettant à l'utilisateur connecté de créer un nouvel article.
 */
@Route(value = "user/create-post", layout = MainLayout.class)
@PageTitle("Créer un post")
public class CreatePostView extends VerticalLayout implements PostPresenter.PostView {

    private final PostPresenter postPresenter;
    private TextField titleField;
    private TextArea contentArea;

    /**
     * Constructeur de la vue de création d'article.
     */
    @Autowired
    public CreatePostView(PostPresenter postPresenter) {
        this.postPresenter = postPresenter;
        this.postPresenter.setView(this);
        configureLayout();
        add(createMainContent());
    }

    /**
     * Configure le layout principal de la vue.
     */
    private void configureLayout() {
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);
    }

    /**
     * Crée le contenu principal de la page (titre, infos utilisateur,
     * formulaire).
     */
    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("100%");
        mainContent.setAlignItems(Alignment.CENTER);

        mainContent.add(
                VaadinUtils.createSeparator("80%"),
                VaadinUtils.createPageTitle("CRÉER UN ARTICLE"),
                VaadinUtils.createSeparator("80%"),
                createUserInfoSection(),
                createPostForm()
        );
        return mainContent;
    }

    /**
     * Affiche les informations de l'utilisateur connecté.
     */
    private Component createUserInfoSection() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String displayName = "Utilisateur inconnu";
        if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String givenName = oidcUser.getGivenName();
            String familyName = oidcUser.getFamilyName();
            if (givenName != null && familyName != null) {
                displayName = givenName + " " + familyName;
            } else if (oidcUser.getFullName() != null) {
                displayName = oidcUser.getFullName();
            } else {
                displayName = oidcUser.getEmail();
            }
        }
        VerticalLayout section = new VerticalLayout();
        section.setWidth("50%");
        section.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BorderRadius.MEDIUM);
        section.setAlignItems(Alignment.CENTER);
        section.add(
                new H3("\uD83D\uDC64 Auteur du post"),
                new Paragraph("Vous allez créer cet article en tant que : " + displayName)
        );
        return section;
    }

    /**
     * Crée le formulaire de création d'article.
     */
    private VerticalLayout createPostForm() {
        titleField = new TextField("Titre");
        titleField.setPlaceholder("Titre de l'article");
        titleField.setWidthFull();

        contentArea = new TextArea("Contenu");
        contentArea.setPlaceholder("Contenu de l'article");
        contentArea.setWidthFull();
        contentArea.setMinHeight("300px");

        Button publishButton = new Button("Publier", e -> publierArticle());
        publishButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout formLayout = new VerticalLayout(titleField, contentArea, publishButton);
        formLayout.setWidth("50%");
        return formLayout;
    }

    /**
     * Logique de publication d'un article après validation du formulaire.
     */
    private void publierArticle() {
        if (!validerFormulaire()) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser)) {
            VaadinUtils.showErrorNotification("Impossible de récupérer les informations de l'utilisateur connecté.");
            return;
        }
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        Post post = new Post();
        post.setTitre(titleField.getValue().trim());
        post.setContenu(contentArea.getValue().trim());
        post.setAuteurEmail(oidcUser.getEmail());
        String givenName = oidcUser.getGivenName();
        String familyName = oidcUser.getFamilyName();
        if (givenName != null && familyName != null) {
            post.setAuteurNom(givenName + " " + familyName);
        } else if (oidcUser.getFullName() != null) {
            post.setAuteurNom(oidcUser.getFullName());
        } else {
            post.setAuteurNom(oidcUser.getEmail());
        }

        postPresenter.publierPost(post);
    }

    /**
     * Valide le formulaire de création d'article.
     */
    private boolean validerFormulaire() {
        ValidationUtils.ValidationResult titleResult = ValidationUtils.validateTitle(titleField);
        if (!titleResult.isValid()) {
            VaadinUtils.showErrorNotification(titleResult.getErrorMessage());
            return false;
        }

        ValidationUtils.ValidationResult contentResult = ValidationUtils.validateContent(contentArea);
        if (!contentResult.isValid()) {
            VaadinUtils.showErrorNotification(contentResult.getErrorMessage());
            return false;
        }

        return true;
    }

    /**
     * Affiche un message de succès et vide le formulaire.
     */
    @Override
    public void afficherMessage(String message) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.showSuccessNotification(message);
            viderFormulaire();
        }));
    }

    /**
     * Affiche un message d'erreur.
     */
    @Override
    public void afficherErreur(String erreur) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.showErrorNotification(erreur);
        }));
    }

    /**
     * Vide le formulaire de création d'article.
     */
    @Override
    public void viderFormulaire() {
        getUI().ifPresent(ui -> ui.access(() -> {
            titleField.clear();
            contentArea.clear();
        }));
    }

    /**
     * Redirige vers la page de détail de l'article nouvellement créé.
     */
    @Override
    public void redirigerVersDetail(Long postId) {
        getUI().ifPresent(ui -> ui.access(() -> {
            ui.navigate("user/article/" + postId);
        }));
    }

    // Méthodes non utilisées de l'interface PostView
    @Override
    public void afficherPost(Post post) {
        // La redirection se fait maintenant après la publication, cette méthode reste inutilisée.
    }

    @Override
    public void afficherPosts(List<Post> posts) {
        // Non utilisé dans cette vue.

    }

}
