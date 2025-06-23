package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.CommentairePresenter;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import app.project_fin_d_etude.utils.ValidationUtils;
import app.project_fin_d_etude.service.UtilisateurService;
import app.project_fin_d_etude.model.Utilisateur;

/**
 * Vue de détail d'un article : affiche le contenu de l'article et ses
 * commentaires. L'affichage est automatique dès le chargement de la page.
 */
@Route(value = "user/article", layout = MainLayout.class)
@PageTitle("Détail de l'article")
public class PostDetailView extends VerticalLayout implements HasUrlParameter<Long>, PostPresenter.PostView, CommentairePresenter.CommentaireView {

    private static final String DATE_FORMAT = "dd MMMM yyyy";
    private static final String NO_COMMENTS_MESSAGE = "Aucun commentaire pour le moment. Soyez le premier à commenter !";
    private static final String COMMENT_PLACEHOLDER = "Écrivez votre commentaire ici...";
    private static final String DEFAULT_AUTHOR_NAME = "Anonyme";

    private final PostPresenter postPresenter;
    private final CommentairePresenter commentairePresenter;
    private final DateTimeFormatter dateFormatter;
    private final UtilisateurService utilisateurService;
    private VerticalLayout commentsSection;
    private TextArea commentTextArea;
    private Button submitButton;
    private Post currentPost;

    @Autowired
    public PostDetailView(PostPresenter postPresenter, CommentairePresenter commentairePresenter, UtilisateurService utilisateurService) {
        this.postPresenter = postPresenter;
        this.commentairePresenter = commentairePresenter;
        this.utilisateurService = utilisateurService;
        this.dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        this.postPresenter.setView(this);
        this.commentairePresenter.setView(this);
    }

    /**
     * Récupère l'identifiant de l'article depuis l'URL et déclenche le
     * chargement.
     */
    @Override
    public void setParameter(BeforeEvent event, Long postId) {
        if (postId == null) {
            getUI().ifPresent(ui -> ui.navigate("articles"));
            VaadinUtils.showErrorNotification("ID d'article invalide");
            return;
        }
        removeAll(); // Nettoyer la vue avant de charger
        VaadinUtils.showLoading(this);
        postPresenter.chargerPost(postId);
    }

    /**
     * Affiche le contenu de l'article et déclenche le chargement des
     * commentaires.
     */
    @Override
    public void afficherPost(Post post) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this); // Cacher le loader principal
            if (post == null) {
                showErrorAndRedirect("Article introuvable ou supprimé");
                return;
            }
            this.currentPost = post;
            renderPostContent(post);
            // Le chargement des commentaires est déclenché ici
            commentairePresenter.chargerCommentaires(post);
        }));
    }

    /**
     * Construit et affiche le contenu principal de l'article.
     */
    private void renderPostContent(Post post) {
        removeAll();

        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("100%");
        mainContent.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
        mainContent.addClassNames(LumoUtility.Padding.Vertical.LARGE);

        mainContent.add(
                createPostHeader(post),
                createPostBody(post.getContenu()),
                createCommentsContainer()
        );

        add(mainContent);
    }

    /**
     * Crée l'en-tête de l'article (titre, métadonnées).
     */
    private VerticalLayout createPostHeader(Post post) {
        VerticalLayout header = new VerticalLayout();
        header.add(
                createPostTitle(post.getTitre()),
                createPostMetadata(post)
        );
        return header;
    }

    /**
     * Crée le titre de l'article.
     */
    private H1 createPostTitle(String title) {
        H1 titleComponent = new H1(title);
        titleComponent.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        titleComponent.getStyle().set("text-align", "center");
        titleComponent.getStyle().set("width", "100%");

        return titleComponent;
    }

    /**
     * Crée les métadonnées de l'article (auteur, date).
     */
    private HorizontalLayout createPostMetadata(Post post) {
        String authorName = Optional.ofNullable(post.getAuteur())
                .map(a -> a.getNom())
                .orElse(DEFAULT_AUTHOR_NAME);
        String dateStr = post.getDatePublication() != null ? post.getDatePublication().format(dateFormatter) : "";

        Paragraph auteurPara = new Paragraph("Par " + authorName);
        auteurPara.getStyle().set("font-weight", "bold").set("margin-right", "1em");
        Paragraph datePara = new Paragraph(dateStr);

        HorizontalLayout metadata = new HorizontalLayout(auteurPara, datePara);
        metadata.setSpacing(true);
        metadata.setPadding(false);
        metadata.setWidthFull();
        metadata.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        metadata.setAlignItems(FlexComponent.Alignment.CENTER);

        return metadata;
    }

    /**
     * Crée le corps de l'article.
     */
    private Div createPostBody(String content) {
        Div contentDiv = new Div(new Paragraph(content));
        contentDiv.getStyle()
                .set("background", "#f7f7fa")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.04)")
                .set("border", "1px solid #e0e0e0")
                .set("padding", "1.2em")
                .set("margin-bottom", "2em")
                .set("width", "70%")
                .set("align-self", "center")
                .set("text-align", "left");

        return contentDiv;
    }

    /**
     * Crée la section des commentaires (formulaire + liste).
     */
    private VerticalLayout createCommentsContainer() {
        H2 commentairesTitle = new H2("Commentaires");
        commentairesTitle.getStyle().set("width", "55%");

        commentsSection = new VerticalLayout();
        commentsSection.setWidth("55%"); // même largeur que le formulaire
        commentsSection.setPadding(false);
        commentsSection.setSpacing(true);

        VerticalLayout container = new VerticalLayout(
                commentairesTitle,
                createCommentInputForm(),
                commentsSection
        );
        container.setWidth("100%");
        container.setAlignItems(FlexComponent.Alignment.CENTER);
        return container;
    }

    /**
     * Crée le formulaire de saisie de commentaire.
     */
    private HorizontalLayout createCommentInputForm() {
        commentTextArea = new TextArea();
        commentTextArea.setPlaceholder(COMMENT_PLACEHOLDER);
        commentTextArea.setWidth("100%");
        commentTextArea.setHeight("35px");

        submitButton = new Button("Publier", e -> handleCommentSubmission());

        HorizontalLayout formLayout = new HorizontalLayout(commentTextArea, submitButton);
        formLayout.setWidth("55%");
        formLayout.setAlignItems(FlexComponent.Alignment.END);
        formLayout.setSpacing(true);

        submitButton.getStyle().set("min-width", "120px");

        formLayout.setFlexGrow(1, commentTextArea);

        return formLayout;
    }

    /**
     * Gère la soumission d'un commentaire (validation, feedback).
     */
    private void handleCommentSubmission() {
        ValidationUtils.ValidationResult validation = ValidationUtils.validateContent(commentTextArea);
        if (!validation.isValid()) {
            VaadinUtils.showErrorNotification(validation.getErrorMessage());
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser oidcUser)) {
            VaadinUtils.showErrorNotification("Impossible de récupérer l'utilisateur connecté.");
            return;
        }
        Utilisateur auteur = utilisateurService.findOrCreateAuteur(oidcUser);

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentTextArea.getValue().trim());
        commentaire.setPost(currentPost);
        commentaire.setAuteur(auteur);
        commentairePresenter.ajouter(commentaire);
    }

    /**
     * Affiche la liste des commentaires ou un message s'il n'y en a pas.
     */
    @Override
    public void afficherCommentaires(List<Commentaire> commentaires) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this);
            commentsSection.removeAll();

            if (commentaires.isEmpty()) {
                commentsSection.add(new Paragraph(NO_COMMENTS_MESSAGE));
            } else {
                commentaires.forEach(comment
                        -> commentsSection.add(createCommentBubble(comment))
                );
            }
        }));
    }

    /**
     * Crée une carte de commentaire.
     */
    private Div createCommentBubble(Commentaire commentaire) {
        Div bubble = new Div();
        bubble.getStyle()
                .set("background", "#f7f7fa")
                .set("border-radius", "8px")
                .set("box-shadow", "0 1px 4px rgba(0,0,0,0.04)")
                .set("border", "1px solid #e0e0e0")
                .set("margin-bottom", "0.5em")
                .set("padding", "1em");

        // Auteur et date en gras, sur une ligne
        String auteur = commentaire.getAuteur() != null ? commentaire.getAuteur().getNom() : "Auteur inconnu";
        String date = commentaire.getDateCreation() != null ? commentaire.getDateCreation().format(dateFormatter) : "";
        Span auteurDate = new Span(auteur + " • " + date);
        auteurDate.getStyle().set("font-weight", "bold").set("font-size", "0.95em").set("display", "block").set("margin-bottom", "0.3em");

        // Contenu du commentaire, en dessous
        Span contenu = new Span(commentaire.getContenu() != null ? commentaire.getContenu() : "");
        contenu.getStyle().set("display", "block");

        bubble.add(auteurDate, contenu);
        return bubble;
    }

    /**
     * Affiche un message de succès et vide le champ commentaire.
     */
    @Override
    public void afficherMessage(String message) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.showSuccessNotification(message);
            commentTextArea.clear();
            rafraichirListe();
        }));
    }

    /**
     * Affiche un message d'erreur.
     */
    @Override
    public void afficherErreur(String erreur) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this);
            VaadinUtils.showErrorNotification(erreur);
        }));
    }

    /**
     * Rafraîchit la liste des commentaires après ajout.
     */
    @Override
    public void rafraichirListe() {
        getUI().ifPresent(ui -> ui.access(() -> {
            if (currentPost != null) {
                postPresenter.chargerPost(currentPost.getId());
            }
        }));
    }

    /**
     * Affiche une erreur et redirige vers la liste des articles.
     */
    private void showErrorAndRedirect(String errorMessage) {
        VaadinUtils.showErrorNotification(errorMessage);
        getUI().ifPresent(ui -> ui.navigate("articles"));
    }

// Méthodes non utilisées de PostView
    @Override
    public void afficherPosts(List<Post> posts) {
    }

    @Override
    public void viderFormulaire() {
    }

    @Override
    public void redirigerVersDetail(Long postId) {
    }
}
