package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
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

    private final PostPresenter postPresenter;
    private final CommentairePresenter commentairePresenter;
    private final DateTimeFormatter dateFormatter;

    private VerticalLayout commentsSection;
    private TextArea commentTextArea;
    private Button submitButton;
    private Post currentPost;

    // Ajout d'une variable d'instance pour suivre le champ de réponse ouvert
    private Div currentlyOpenedReplyDiv = null;

    @Autowired
    public PostDetailView(PostPresenter postPresenter, CommentairePresenter commentairePresenter) {
        this.postPresenter = postPresenter;
        this.commentairePresenter = commentairePresenter;
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
    private H3 createPostTitle(String title) {
        H3 titleComponent = new H3(title);
        titleComponent.addClassName("post-detail-title");
        titleComponent.getStyle().remove("text-align");
        titleComponent.getStyle().remove("width");
        titleComponent.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );

        return titleComponent;
    }

    /**
     * Crée les métadonnées de l'article (auteur, date).
     */
    private HorizontalLayout createPostMetadata(Post post) {
        String authorName = post.getAuteurNom() != null && !post.getAuteurNom().isBlank() ? post.getAuteurNom() : "Auteur inconnu";
        String dateStr = post.getDatePublication() != null ? post.getDatePublication().format(dateFormatter) : "";

        Paragraph auteurPara = new Paragraph("Par " + authorName);
        auteurPara.addClassName("post-detail-meta-author");
        auteurPara.getStyle().remove("font-weight");
        auteurPara.getStyle().remove("margin-right");
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
        contentDiv.addClassName("post-detail-body");
        contentDiv.getStyle().clear();
        return contentDiv;
    }

    /**
     * Crée la section des commentaires (formulaire + liste).
     */
    private VerticalLayout createCommentsContainer() {
        H2 commentairesTitle = new H2("Commentaires");
        commentairesTitle.addClassName("post-detail-comments-title");
        commentairesTitle.getStyle().remove("width");

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
        if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser)) {
            VaadinUtils.showErrorNotification("Impossible de récupérer l'utilisateur connecté.");
            return;
        }
        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        Commentaire commentaire = new Commentaire();
        commentaire.setContenu(commentTextArea.getValue().trim());
        commentaire.setPost(currentPost);
        commentaire.setAuteurEmail(oidcUser.getEmail());
        String givenName = oidcUser.getGivenName();
        String familyName = oidcUser.getFamilyName();
        if (givenName != null && familyName != null) {
            commentaire.setAuteurNom(givenName + " " + familyName);
        } else if (oidcUser.getFullName() != null) {
            commentaire.setAuteurNom(oidcUser.getFullName());
        } else {
            commentaire.setAuteurNom(oidcUser.getEmail());
        }
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
                // Afficher seulement les commentaires principaux (parent == null)
                commentaires.stream()
                        .filter(c -> c.getParent() == null)
                        .forEach(comment -> commentsSection.add(createCommentBubbleWithReplies(comment, commentaires, 0)));
            }
        }));
    }

    // Nouvelle méthode pour afficher un commentaire et ses réponses imbriquées
    private Div createCommentBubbleWithReplies(Commentaire commentaire, List<Commentaire> allCommentaires, int niveau) {
        Div bubble = createCommentBubble(commentaire);

        // Ajout du bouton "Répondre"
        Button repondreBtn = new Button("Répondre");
        bubble.add(repondreBtn);

        // Champ de réponse caché par défaut
        TextArea reponseArea = new TextArea();
        reponseArea.setPlaceholder("Votre réponse...");
        reponseArea.setVisible(false);
        reponseArea.setHeight("40px"); // Champ plus petit
        reponseArea.getStyle().setWidth("400px");
        Button envoyerBtn = new Button("Envoyer");
        envoyerBtn.setVisible(false);

        // Div englobant le champ de réponse et le bouton envoyer
        Div replyDiv = new Div(reponseArea, envoyerBtn);
        replyDiv.setVisible(false);
        bubble.add(replyDiv);

        repondreBtn.addClickListener(e -> {
            // Fermer le champ de réponse précédemment ouvert
            if (currentlyOpenedReplyDiv != null && currentlyOpenedReplyDiv != replyDiv) {
                currentlyOpenedReplyDiv.setVisible(false);
                // Réafficher le bouton répondre du précédent (si besoin)
                if (currentlyOpenedReplyDiv.getParent().isPresent()) {
                    Div parentBubble = (Div) currentlyOpenedReplyDiv.getParent().get();
                    parentBubble.getChildren()
                            .filter(c -> c instanceof Button && ((Button) c).getText().equals("Répondre"))
                            .findFirst()
                            .ifPresent(btn -> btn.setVisible(true));
                }
            }
            // Afficher le champ de réponse courant
            replyDiv.setVisible(true);
            reponseArea.setVisible(true);
            envoyerBtn.setVisible(true);
            repondreBtn.setVisible(false); // Cacher le bouton répondre
            currentlyOpenedReplyDiv = replyDiv;
        });

        envoyerBtn.addClickListener(ev -> {
            String contenu = reponseArea.getValue();
            if (contenu != null && !contenu.trim().isEmpty()) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof OidcUser) {
                    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                    String auteurNom = oidcUser.getFullName() != null ? oidcUser.getFullName() : oidcUser.getEmail();
                    String auteurEmail = oidcUser.getEmail();
                    postPresenter.repondreAuCommentaire(currentPost.getId(), commentaire.getId(), contenu, auteurNom, auteurEmail);
                }
            }
            // Fermer le champ de réponse après envoi
            replyDiv.setVisible(false);
            repondreBtn.setVisible(true);
            reponseArea.clear();
            reponseArea.getStyle().setWidth("300px");
            currentlyOpenedReplyDiv = null;
        });

        // Afficher les réponses (enfants)
        allCommentaires.stream()
                .filter(rep -> rep.getParent() != null && rep.getParent().getId().equals(commentaire.getId()))
                .forEach(rep -> {
                    Div replyChildDiv = createCommentBubbleWithReplies(rep, allCommentaires, niveau + 1);
                    replyChildDiv.getStyle().set("margin-left", (niveau + 1) * 30 + "px");
                    bubble.add(replyChildDiv);
                });

        return bubble;
    }

    /**
     * Crée une carte de commentaire.
     */
    private Div createCommentBubble(Commentaire commentaire) {
        Div bubble = new Div();
        bubble.addClassName("post-detail-comment-bubble");
        bubble.getStyle().clear();

        // Auteur et date
        String auteur = commentaire.getAuteurNom() != null ? commentaire.getAuteurNom() : "Auteur inconnu";
        String date = commentaire.getDateCreation() != null ? commentaire.getDateCreation().format(dateFormatter) : "";
        Span auteurDate = new Span(auteur + " • " + date);
        auteurDate.addClassName("post-detail-comment-author-date");
        auteurDate.getStyle().clear();
        auteurDate.getStyle().set("font-size", "0.95em").set("display", "block").set("margin-bottom", "0.3em");

        // Label inapproprié si besoin
        Span inapproprieLabel = null;
        if (commentaire.isInapproprie()) {
            inapproprieLabel = new Span("Inapproprié");
            inapproprieLabel.addClassName("post-detail-comment-inappropriate");
            inapproprieLabel.getStyle().clear();
            inapproprieLabel.getStyle()
                    .set("color", "white")
                    .set("background", "#d32f2f")
                    .set("padding", "0.2em 0.7em")
                    .set("border-radius", "8px")
                    .set("font-size", "0.85em")
                    .set("margin-left", "1em");
        }

        // Contenu du commentaire
        Span contenu = new Span(commentaire.getContenu() != null ? commentaire.getContenu() : "");
        contenu.addClassName("post-detail-comment-content");
        contenu.getStyle().clear();
        contenu.getStyle().set("display", "block");

        // Ajout dans la bulle
        HorizontalLayout header = new HorizontalLayout(auteurDate);
        if (inapproprieLabel != null) {
            header.add(inapproprieLabel);
        }
        header.setAlignItems(FlexComponent.Alignment.CENTER);

        bubble.add(header, contenu);
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
