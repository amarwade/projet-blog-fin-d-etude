package app.project_fin_d_etude.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Commentaire;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.PostService;
import com.vaadin.flow.component.notification.Notification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import app.project_fin_d_etude.service.CommentaireService;
import app.project_fin_d_etude.utils.VaadinUtils;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * Vue de détail d'un article : affiche le contenu de l'article et ses
 * commentaires. L'affichage est automatique dès le chargement de la page.
 */
@Route(value = "user/article", layout = MainLayout.class)
@PageTitle("Détail de l'article")
public class PostDetailView extends VerticalLayout implements HasUrlParameter<Long> {

    private final PostService postService;
    private final CommentaireService commentaireService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private Post currentPost;
    private VerticalLayout champReponseOuvert = null;

    @Autowired
    public PostDetailView(PostService postService, CommentaireService commentaireService) {
        this.postService = postService;
        this.commentaireService = commentaireService;
        add(createMainSection());
    }

    private VerticalLayout createMainSection() {
        final VerticalLayout mainSection = new VerticalLayout();
        mainSection.setWidth("100%");
        mainSection.setAlignItems(Alignment.CENTER);
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

    /**
     * Crée le titre principal de la page profil.
     */
    private H3 createMainTitle() {
        final H3 title = new H3("Details de l'article");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontWeight.BOLD
        );
        return title;
    }

    @Override
    public void setParameter(BeforeEvent event, Long postId) {
        removeAll();
        Post post = null;
        try {
            post = postService.getPostWithCommentaires(postId).orElse(null);
        } catch (Exception e) {
            add(new Paragraph("Erreur lors de la récupération du post : " + e.getMessage()));
        }
        if (post != null) {
            this.currentPost = post;
            // Titre principal centré avec traits
            VerticalLayout titreSection = new VerticalLayout();

            titreSection.setWidthFull();
            titreSection.setAlignItems(Alignment.CENTER);
            titreSection.getStyle().set("margin-top", "30px").set("margin-bottom", "18px");
            add(titreSection);

            H2 titre = new H2(post.getTitre());
            titre.addClassName("post-detail-title");

            // Affichage de l'auteur et de la date de publication
            String auteur = post.getAuteurNom() != null ? post.getAuteurNom() : "Auteur inconnu";
            String date = post.getDatePublication() != null ? post.getDatePublication().format(dateFormatter) : "";
            Paragraph meta = new Paragraph(auteur + (date.isEmpty() ? "" : " • " + date));
            meta.addClassName("post-detail-meta-author");

            // Contenu de l'article dans une bulle centrée
            Paragraph contenu = new Paragraph(post.getContenu());
            contenu.addClassName("post-detail-body");

            // AJOUT : afficher titre, meta et contenu avant le formulaire de commentaire
            add(titre, meta, contenu);

            // Formulaire de commentaire centré sous l'article
            HorizontalLayout formLayout = new HorizontalLayout();
            formLayout.setWidth("55%");
            formLayout.setHeight("40px");
            formLayout.getStyle().set("margin", "0 auto 2em auto");
            formLayout.setAlignItems(Alignment.END);

            TextArea commentField = new TextArea();
            commentField.setPlaceholder("Écrivez votre commentaire ici...");
            commentField.setWidthFull();
            commentField.setHeight("40px");

            Button publierBtn = new Button("Publier");

            publierBtn.addClickListener(e -> {
                String contenuCommentaire = commentField.getValue();
                if (contenuCommentaire == null || contenuCommentaire.trim().isEmpty()) {
                    Notification.show("Le commentaire ne peut pas être vide.", 3000, Notification.Position.MIDDLE);
                    return;
                }
                // Récupérer l'utilisateur connecté
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser)) {
                    Notification.show("Utilisateur non authentifié.", 3000, Notification.Position.MIDDLE);
                    return;
                }
                OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                Commentaire commentaire = new Commentaire();
                commentaire.setContenu(contenuCommentaire.trim());
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
                try {
                    commentaireService.save(commentaire);
                    Notification.show("Commentaire publié !", 3000, Notification.Position.MIDDLE);
                    commentField.clear();
                    removeAll();
                    setParameter(null, currentPost.getId());
                } catch (Exception ex) {
                    Notification.show("Erreur lors de la publication : " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                }
            });

            formLayout.add(commentField, publierBtn);
            formLayout.setFlexGrow(1, commentField);
            add(formLayout);

            afficherCommentaires(post.getCommentaires());
        } else {
            add(new Paragraph("Aucun article trouvé pour l'ID : " + postId));
        }
    }

    private void afficherCommentaires(List<Commentaire> commentaires) {
        VerticalLayout commentsContainer = new VerticalLayout();
        commentsContainer.setWidth("55%");
        commentsContainer.getStyle().set("margin", "0 auto");
        commentsContainer.setPadding(false);
        commentsContainer.setSpacing(true);
        commentsContainer.setAlignItems(Alignment.START);

        H2 titreCommentaires = new H2("Commentaires");
        commentsContainer.add(titreCommentaires);

        if (commentaires != null && !commentaires.isEmpty()) {
            // Afficher seulement les commentaires principaux (parent == null)
            commentaires.stream()
                    .filter(c -> c.getParent() == null)
                    .forEach(commentaire -> commentsContainer.add(creerBulleCommentaire(commentaire, commentaires, 0)));
        } else {
            commentsContainer.add(new Paragraph("Aucun commentaire pour cet article."));
        }
        add(commentsContainer);
    }

    private VerticalLayout creerBulleCommentaire(Commentaire commentaire, List<Commentaire> tous, int niveau) {
        VerticalLayout bulle = new VerticalLayout();
        bulle.setSpacing(false);
        bulle.setPadding(false);
        bulle.addClassName("post-detail-comment-bubble");
        bulle.addClassName("level-" + niveau);
        bulle.getStyle().set("margin-left", (niveau * 32) + "px");

        Icon userIcon = VaadinIcon.USER.create();
        userIcon.setSize("18px");
        userIcon.getStyle().set("color", "#1976d2");
        String auteur = commentaire.getAuteurNom() != null ? commentaire.getAuteurNom() : "Auteur inconnu";
        String date = commentaire.getDateCreation() != null ? commentaire.getDateCreation().format(dateFormatter) : "";
        Span auteurSpan = new Span(auteur);
        auteurSpan.getStyle().set("font-weight", "bold").set("color", "#1976d2");
        Span dateSpan = new Span(date);
        dateSpan.getStyle().set("font-size", "0.9em").set("color", "#888").set("margin-left", "8px");
        HorizontalLayout auteurDate = new HorizontalLayout(userIcon, auteurSpan, dateSpan);
        auteurDate.setSpacing(true);
        auteurDate.addClassName("post-detail-comment-author-date");
        auteurDate.setAlignItems(Alignment.CENTER);

        // Badge inapproprié si besoin
        if (commentaire.isInapproprie()) {
            Span badge = new Span("Inapproprié");
            badge.getStyle()
                    .set("background", "#ffebee")
                    .set("color", "#b71c1c")
                    .set("padding", "2px 10px")
                    .set("border-radius", "10px")
                    .set("font-size", "0.9em")
                    .set("font-weight", "bold")
                    .set("margin-left", "10px");
            auteurDate.add(badge);
        }

        Paragraph contenu = new Paragraph(commentaire.getContenu());
        contenu.addClassName("post-detail-comment-content");

        Button repondreBtn = new Button("Répondre");
        repondreBtn.addClassName("comment-action");

        HorizontalLayout btnLayout = new HorizontalLayout(repondreBtn);
        btnLayout.setWidthFull();
        btnLayout.setJustifyContentMode(JustifyContentMode.END);
        btnLayout.setPadding(false);
        btnLayout.setSpacing(false);

        VerticalLayout reponseLayout = new VerticalLayout();
        reponseLayout.setVisible(false);
        reponseLayout.setPadding(false);
        reponseLayout.setSpacing(false);
        reponseLayout.setWidthFull();

        TextField reponseField = new TextField();
        reponseField.setPlaceholder("Votre réponse...");
        reponseField.setWidthFull();

        Button publierReponseBtn = new Button("Publier");
        Button annulerBtn = new Button("Annuler");

        HorizontalLayout reponseForm = new HorizontalLayout(reponseField, publierReponseBtn, annulerBtn);
        reponseForm.setWidthFull();
        reponseForm.setAlignItems(Alignment.END);
        reponseForm.setFlexGrow(1, reponseField);

        reponseLayout.add(reponseForm);

        repondreBtn.addClickListener(e -> {
            if (champReponseOuvert != null && champReponseOuvert != reponseLayout) {
                champReponseOuvert.setVisible(false);
                champReponseOuvert.getParent().ifPresent(parent -> {
                    if (parent instanceof VerticalLayout parentLayout) {
                        parentLayout.getChildren()
                                .filter(c -> c instanceof HorizontalLayout)
                                .findFirst()
                                .ifPresent(b -> b.setVisible(true));
                    }
                });
            }
            btnLayout.setVisible(false);
            reponseLayout.setVisible(true);
            champReponseOuvert = reponseLayout;
            reponseField.focus();
        });

        annulerBtn.addClickListener(e -> {
            reponseLayout.setVisible(false);
            btnLayout.setVisible(true);
            reponseField.clear();
            champReponseOuvert = null;
        });

        publierReponseBtn.addClickListener(e -> {
            String contenuReponse = reponseField.getValue();
            if (contenuReponse == null || contenuReponse.trim().isEmpty()) {
                Notification.show("La réponse ne peut pas être vide.", 3000, Notification.Position.MIDDLE);
                return;
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof OidcUser)) {
                Notification.show("Utilisateur non authentifié.", 3000, Notification.Position.MIDDLE);
                return;
            }

            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            Commentaire reponse = new Commentaire();
            reponse.setContenu(contenuReponse.trim());
            reponse.setPost(currentPost);
            reponse.setParent(commentaire);
            reponse.setAuteurEmail(oidcUser.getEmail());
            String givenName = oidcUser.getGivenName();
            String familyName = oidcUser.getFamilyName();
            if (givenName != null && familyName != null) {
                reponse.setAuteurNom(givenName + " " + familyName);
            } else if (oidcUser.getFullName() != null) {
                reponse.setAuteurNom(oidcUser.getFullName());
            } else {
                reponse.setAuteurNom(oidcUser.getEmail());
            }

            try {
                commentaireService.save(reponse);
                Notification.show("Réponse publiée !", 3000, Notification.Position.MIDDLE);
                reponseField.clear();
                reponseLayout.setVisible(false);
                btnLayout.setVisible(true);
                champReponseOuvert = null;
                removeAll();
                setParameter(null, currentPost.getId());
            } catch (Exception ex) {
                Notification.show("Erreur lors de la publication de la réponse : " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
            }
        });

        bulle.add(auteurDate, contenu, btnLayout, reponseLayout);

        tous.stream()
                .filter(rep -> rep.getParent() != null && rep.getParent().getId().equals(commentaire.getId()))
                .forEach(rep -> bulle.add(creerBulleCommentaire(rep, tous, niveau + 1)));

        return bulle;
    }

}
