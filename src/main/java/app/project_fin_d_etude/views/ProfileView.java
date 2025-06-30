package app.project_fin_d_etude.views;

import java.util.List;
import java.util.ArrayList;

import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.shared.Registration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.service.PostService;
import app.project_fin_d_etude.utils.AsyncDataLoader;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue de profil utilisateur : affiche les informations Keycloak de
 * l'utilisateur connecté.
 */
@Route(value = "user/profile", layout = MainLayout.class)
@PageTitle("Mon Profil")
@AnonymousAllowed
public class ProfileView extends VerticalLayout {

    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);
    private static final String NO_PROFILE_INFO = "Aucune information de profil disponible. Veuillez vous reconnecter.";
    private static final String NO_ARTICLES = "Vous n'avez publié aucun article.";
    private static final String ERROR_LOADING = "Erreur lors du chargement de vos articles.";

    private final PostService postService;
    private final AsyncDataLoader asyncDataLoader;
    private final FlexLayout postsLayout;
    private VerticalLayout content;

    @Autowired
    public ProfileView(PostService postService, AsyncDataLoader asyncDataLoader) {
        this.postService = postService;
        this.asyncDataLoader = asyncDataLoader;
        this.postsLayout = new FlexLayout();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Background.CONTRAST_5, "profile-view");
        getStyle().remove("padding-top");

        add(createMainSection());
        content = createProfileContent();
        add(content);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        logger.info("onAttach appelé, initialAttach: {}", attachEvent.isInitialAttach());
        if (attachEvent.isInitialAttach()) {
            logger.info("Début du chargement du profil utilisateur");
            loadUserArticlesRobuste(attachEvent);
        }
    }

    /**
     * Crée la section principale (titre, séparateurs).
     */
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
        final H3 title = new H3("INFORMATIONS PERSONNELLES");
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
     * Affiche d'abord les infos utilisateur, puis ses articles sous forme de
     * cartes.
     */
    private VerticalLayout createProfileContent() {
        final VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            // Bloc infos utilisateur
            VerticalLayout userInfo = new VerticalLayout();
            userInfo.setAlignItems(Alignment.START);
            userInfo.setWidth("100%");
            userInfo.setMaxWidth("800px");
            userInfo.addClassNames(LumoUtility.Background.BASE, LumoUtility.BorderRadius.LARGE, LumoUtility.Padding.LARGE, LumoUtility.BoxShadow.MEDIUM, "profile-user-info");
            userInfo.getStyle().remove("border").remove("margin-bottom");
            String nom = oidcUser.getGivenName();
            String prenom = oidcUser.getFamilyName();
            String email = oidcUser.getEmail();
            String username = oidcUser.getPreferredUsername();
            userInfo.add(new H2("Profil Utilisateur"));
            userInfo.add(VaadinUtils.createSeparator("100%"));
            userInfo.add(new Paragraph("Nom : " + (nom != null ? nom : "Non renseigné")));
            userInfo.add(new Paragraph("Prénom : " + (prenom != null ? prenom : "Non renseigné")));
            userInfo.add(new Paragraph("Email : " + (email != null ? email : "Non renseigné")));
            userInfo.add(new Paragraph("Nom d'utilisateur : " + (username != null ? username : "Non renseigné")));
            content.add(userInfo);

            // Bloc articles utilisateur (asynchrone)
            content.add(new H2("Mes articles publiés"));
            configurePostsLayout();
            content.add(postsLayout);
        } else {
            content.add(new Paragraph(NO_PROFILE_INFO));
        }
        return content;
    }

    private void configurePostsLayout() {
        postsLayout.setWidthFull();
        postsLayout.setFlexWrap(FlexLayout.FlexWrap.WRAP);
        postsLayout.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
        postsLayout.getStyle()
                .set("max-width", "100%")
                .set("margin", "32px auto 0 auto")
                .set("padding", "16px")
                .set("box-sizing", "border-box")
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("justify-content", "center");
    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        return super.addAttachListener(listener);
    }

    private void loadUserArticlesRobuste(AttachEvent attachEvent) {
        postsLayout.removeAll();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            String email = oidcUser.getEmail();
            if (email != null && !email.isBlank()) {
                try {
                    logger.info("Chargement synchrone des articles de l'utilisateur {}", email);
                    List<Post> posts = postService.getPostsByAuteurEmail(email);
                    if (posts == null || posts.isEmpty()) {
                        postsLayout.add(new Paragraph(NO_ARTICLES));
                    } else {
                        for (Post post : posts) {
                            BlogPostCard card = new BlogPostCard(post);
                            card.setWidth("320px");
                            card.getStyle().set("min-width", "260px").set("max-width", "340px");
                            postsLayout.add(card);
                        }
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du chargement des articles de l'utilisateur " + email, e);
                    postsLayout.add(new Paragraph(ERROR_LOADING));
                }
            } else {
                postsLayout.add(new Paragraph(NO_ARTICLES));
            }
        } else {
            postsLayout.add(new Paragraph(NO_PROFILE_INFO));
        }
    }

}
