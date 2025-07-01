package app.project_fin_d_etude.views;

import java.util.List;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

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
    private VerticalLayout postsContainer;

    @Autowired
    private Executor taskExecutor;

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

        postsContainer = new VerticalLayout();
        postsContainer.setPadding(false);
        postsContainer.setSpacing(false);
        postsContainer.setWidthFull();
        postsContainer.setAlignItems(Alignment.CENTER);
        add(postsContainer);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        logger.info("onAttach appelé, initialAttach: {}", attachEvent.isInitialAttach());
        if (attachEvent.isInitialAttach()) {
            logger.info("Début du chargement des articles utilisateur (asynchrone avec push)");
            postsContainer.removeAll();
            Paragraph loadingMessage = new Paragraph("Chargement de vos articles en cours...");
            loadingMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE);
            postsContainer.add(loadingMessage);
            SecurityContext context = SecurityContextHolder.getContext();
            taskExecutor.execute(() -> {
                logger.info("[ASYNC] Thread démarré pour chargement articles profil");
                SecurityContextHolder.setContext(context);
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    logger.info("[ASYNC] Authentication récupérée: {}", authentication != null ? authentication.getName() : "null");
                    if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser) {
                        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
                        String email = oidcUser.getEmail();
                        logger.info("[ASYNC] Email utilisateur: {}", email);
                        List<Post> posts = postService.getPostsByAuteurEmail(email);
                        logger.info("[ASYNC] Articles récupérés: {}", posts != null ? posts.size() : 0);
                        getUI().ifPresent(ui -> ui.access(() -> {
                            logger.info("[ASYNC] Accès UI pour mise à jour du DOM");
                            postsContainer.removeAll();
                            if (posts == null || posts.isEmpty()) {
                                Paragraph emptyMsg = new Paragraph(NO_ARTICLES);
                                emptyMsg.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER, LumoUtility.FontSize.LARGE, LumoUtility.Margin.Top.XLARGE);
                                postsContainer.add(emptyMsg);
                                logger.info("[ASYNC] Message aucun article affiché");
                            } else {
                                FlexLayout grid = new FlexLayout();
                                grid.setWidthFull();
                                grid.setFlexWrap(FlexLayout.FlexWrap.WRAP);
                                grid.setJustifyContentMode(FlexLayout.JustifyContentMode.CENTER);
                                grid.addClassName("profile-articles-grid");
                                posts.forEach(post -> {
                                    BlogPostCard card = new BlogPostCard(post);
                                    grid.add(card);
                                });
                                postsContainer.add(grid);
                                logger.info("[ASYNC] Articles affichés dans le DOM");
                            }
                            ui.push();
                            logger.info("[ASYNC] ui.push() appelé pour forcer le rafraîchissement");
                        }));
                    } else {
                        logger.warn("[ASYNC] Utilisateur non authentifié dans le thread");
                        getUI().ifPresent(ui -> ui.access(() -> {
                            postsContainer.removeAll();
                            postsContainer.add(new Paragraph(NO_PROFILE_INFO));
                            ui.push();
                        }));
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du chargement asynchrone des articles utilisateur", e);
                    getUI().ifPresent(ui -> ui.access(() -> {
                        postsContainer.removeAll();
                        Paragraph errorMsg = new Paragraph(ERROR_LOADING + " (" + e.getMessage() + ")");
                        errorMsg.getStyle().set("color", "orange").set("font-weight", "bold").set("font-size", "1.2em");
                        postsContainer.add(errorMsg);
                        ui.push();
                    }));
                }
            });
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
            content.add(new H2("Mes articles publiés"));
        } else {
            content.add(new Paragraph(NO_PROFILE_INFO));
        }
        return content;
    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        return super.addAttachListener(listener);
    }

}
