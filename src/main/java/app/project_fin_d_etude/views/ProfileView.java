package app.project_fin_d_etude.views;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import com.vaadin.flow.server.auth.AnonymousAllowed;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.components.BlogPostCard;
import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.UserProfilePresenter;
import app.project_fin_d_etude.service.PostService;
import app.project_fin_d_etude.utils.AsyncDataLoader;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue de profil utilisateur complète : affiche les informations Keycloak, les
 * articles publiés, et permet la modification du profil et du mot de passe.
 */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Mon Profil - Blog d'entreprise")
@AnonymousAllowed
public class ProfileView extends VerticalLayout implements UserProfilePresenter.UserProfileView {

    private static final Logger logger = LoggerFactory.getLogger(ProfileView.class);
    private static final String NO_PROFILE_INFO = "Aucune information de profil disponible. Veuillez vous reconnecter.";
    private static final String NO_ARTICLES = "Vous n'avez publié aucun article.";
    private static final String ERROR_LOADING = "Erreur lors du chargement de vos articles.";

    private final PostService postService;
    private final AsyncDataLoader asyncDataLoader;
    private final UserProfilePresenter userProfilePresenter;
    private final FlexLayout postsLayout;

    private VerticalLayout content;
    private VerticalLayout postsContainer;
    private VerticalLayout profileEditContainer;
    private VerticalLayout passwordChangeContainer;

    // Champs pour les informations personnelles
    private TextField firstNameField;
    private TextField lastNameField;
    private EmailField emailField;
    private TextField departmentField;
    private TextField positionField;
    private TextField phoneExtensionField;
    private TextField officeLocationField;
    private Button updateProfileButton;

    // Champs pour le changement de mot de passe
    private PasswordField currentPasswordField;
    private PasswordField newPasswordField;
    private PasswordField confirmPasswordField;
    private Button changePasswordButton;

    @Autowired
    private Executor taskExecutor;

    @Autowired
    public ProfileView(PostService postService, AsyncDataLoader asyncDataLoader, UserProfilePresenter userProfilePresenter) {
        this.postService = postService;
        this.asyncDataLoader = asyncDataLoader;
        this.userProfilePresenter = userProfilePresenter;
        this.userProfilePresenter.setView(this);
        this.postsLayout = new FlexLayout();

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.START);
        addClassNames(LumoUtility.Padding.LARGE, LumoUtility.Background.CONTRAST_5, "profile-view");

        initView();
    }

    private void initView() {
        // Titre principal
        H2 title = new H2("Mon Profil");
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        add(title);

        // Création des onglets
        Tab infoTab = new Tab("Informations");
        Tab articlesTab = new Tab("Mes Articles");
        Tab editTab = new Tab("Modifier Profil");
        Tab passwordTab = new Tab("Changer Mot de Passe");

        Tabs tabs = new Tabs(infoTab, articlesTab, editTab, passwordTab);
        tabs.setWidth(null);
        tabs.getStyle().set("margin", "0 auto");
        add(tabs);

        // Contenu des onglets
        VerticalLayout infoContent = createInfoTab();
        VerticalLayout articlesContent = createArticlesTab();
        VerticalLayout editContent = createEditTab();
        VerticalLayout passwordContent = createPasswordTab();

        // Association des onglets au contenu
        tabs.addSelectedChangeListener(event -> {
            removeAll();
            add(title, tabs);

            Tab selectedTab = event.getSelectedTab();
            if (selectedTab == infoTab) {
                add(infoContent);
            } else if (selectedTab == articlesTab) {
                add(articlesContent);
            } else if (selectedTab == editTab) {
                add(editContent);
            } else if (selectedTab == passwordTab) {
                add(passwordContent);
            }
        });

        // Onglet par défaut
        add(infoContent);
    }

    private VerticalLayout createInfoTab() {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

            // Bloc infos utilisateur amélioré
            VerticalLayout userInfo = new VerticalLayout();
            userInfo.setAlignItems(Alignment.START);
            userInfo.setWidth("100%");
            userInfo.setMaxWidth("600px");
            userInfo.getStyle()
                    .set("background", "#f9f9fc")
                    .set("border-radius", "16px")
                    .set("box-shadow", "0 2px 12px 0 rgba(44,62,80,0.07)")
                    .set("border", "1.5px solid #4f8cff")
                    .set("padding", "32px 32px 24px 32px");

            H3 sectionTitle = new H3("Informations Personnelles");
            sectionTitle.getStyle()
                    .set("font-size", "2rem")
                    .set("font-weight", "bold")
                    .set("color", "#223366")
                    .set("margin-bottom", "8px");
            userInfo.add(sectionTitle);
            userInfo.add(VaadinUtils.createSeparator("100%"));

            userInfo.add(createInfoRow("Nom", oidcUser.getGivenName()));
            userInfo.add(createInfoRow("Prénom", oidcUser.getFamilyName()));
            userInfo.add(createInfoRow("Email", oidcUser.getEmail()));
            userInfo.add(createInfoRow("Nom d'utilisateur", oidcUser.getPreferredUsername()));

            content.add(userInfo);
        } else {
            content.add(new Paragraph(NO_PROFILE_INFO));
        }

        return content;
    }

    // Méthode utilitaire pour styliser chaque ligne d'information
    private HorizontalLayout createInfoRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setAlignItems(Alignment.CENTER);
        row.setSpacing(true);
        row.setWidthFull();
        com.vaadin.flow.component.html.Span labelSpan = new com.vaadin.flow.component.html.Span(label + " : ");
        labelSpan.getStyle().set("font-weight", "bold").set("color", "#2d3a4a").set("min-width", "160px");
        com.vaadin.flow.component.html.Span valueSpan = new com.vaadin.flow.component.html.Span(value != null ? value : "Non renseigné");
        valueSpan.getStyle().set("color", "#223366").set("font-size", "1.08rem");
        row.add(labelSpan, valueSpan);
        return row;
    }

    private VerticalLayout createArticlesTab() {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        postsContainer = new VerticalLayout();
        postsContainer.setPadding(false);
        postsContainer.setSpacing(false);
        postsContainer.setWidthFull();
        postsContainer.setAlignItems(Alignment.CENTER);
        content.add(postsContainer);

        return content;
    }

    private VerticalLayout createEditTab() {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        // Section informations personnelles
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle().set("border", "1px solid #ccc");
        section.getStyle().set("border-radius", "8px");
        section.setMaxWidth("600px");

        H3 sectionTitle = new H3("Modifier mon email");
        section.add(sectionTitle);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Champ unique : email
        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setPlaceholder("votre.email@entreprise.com");

        updateProfileButton = new Button("Mettre à jour le profil");
        updateProfileButton.addClickListener(e -> updateProfile());

        // Ajout du champ au formulaire
        form.add(emailField);
        form.add(updateProfileButton);

        section.add(form);
        content.add(section);

        // Charger les informations actuelles
        userProfilePresenter.chargerProfil();

        return content;
    }

    private VerticalLayout createPasswordTab() {
        VerticalLayout content = new VerticalLayout();
        content.setWidthFull();
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);
        content.setPadding(false);

        // Section changement de mot de passe
        VerticalLayout section = new VerticalLayout();
        section.setPadding(true);
        section.setSpacing(true);
        section.getStyle().set("border", "1px solid #ccc");
        section.getStyle().set("border-radius", "8px");
        section.setMaxWidth("600px");

        H3 sectionTitle = new H3("Changer mon mot de passe");
        section.add(sectionTitle);

        FormLayout form = new FormLayout();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("600px", 2)
        );

        // Champs du formulaire
        currentPasswordField = new PasswordField("Mot de passe actuel");
        currentPasswordField.setRequired(true);

        newPasswordField = new PasswordField("Nouveau mot de passe");
        newPasswordField.setRequired(true);
        newPasswordField.setHelperText("Au moins 8 caractères, avec majuscule, minuscule et chiffre");

        confirmPasswordField = new PasswordField("Confirmer le nouveau mot de passe");
        confirmPasswordField.setRequired(true);

        changePasswordButton = new Button("Changer le mot de passe");
        changePasswordButton.addClickListener(e -> changePassword());

        // Ajout des champs au formulaire
        form.add(currentPasswordField);
        form.add(newPasswordField, confirmPasswordField);
        form.add(changePasswordButton);

        section.add(form);
        content.add(section);

        return content;
    }

    private void updateProfile() {
        // Seul l'email est modifiable désormais
        String email = emailField.getValue();
        // On passe des valeurs vides pour prénom et nom (non utilisés)
        boolean success = userProfilePresenter.mettreAJourProfilSynchrone("", "", email);
        if (success) {
            afficherMessage("Profil mis à jour avec succès");
            viderFormulaire();
            rafraichirProfil();
        } else {
            afficherErreur("Impossible de mettre à jour le profil");
        }
    }

    private void changePassword() {
        String currentPassword = currentPasswordField.getValue();
        String newPassword = newPasswordField.getValue();
        String confirmPassword = confirmPasswordField.getValue();
        // Appel synchrone
        boolean success = userProfilePresenter.changerMotDePasseSynchrone(currentPassword, newPassword, confirmPassword);
        if (success) {
            afficherMessage("Mot de passe changé avec succès");
            viderFormulaire();
            rafraichirProfil();
        } else {
            afficherErreur("Impossible de changer le mot de passe");
        }
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        logger.info("onAttach appelé, initialAttach: {}", attachEvent.isInitialAttach());
        if (attachEvent.isInitialAttach()) {
            loadUserArticles();
        }
    }

    private void loadUserArticles() {
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

    // Implémentation des méthodes de l'interface UserProfileView
    @Override
    public void afficherProfil(Map<String, String> profile) {
        // Remplir les champs avec les informations du profil
        firstNameField.setValue(profile.getOrDefault("firstName", ""));
        lastNameField.setValue(profile.getOrDefault("lastName", ""));
        emailField.setValue(profile.getOrDefault("email", ""));
    }

    @Override
    public void afficherMessage(String message) {
        Notification.show(message, 3000, Notification.Position.TOP_CENTER);
    }

    @Override
    public void afficherErreur(String erreur) {
        Notification.show(erreur, 5000, Notification.Position.TOP_CENTER);
    }

    @Override
    public void viderFormulaire() {
        // Vider les champs du formulaire de modification du profil
        if (firstNameField != null) {
            firstNameField.clear();
        }
        if (lastNameField != null) {
            lastNameField.clear();
        }
        if (emailField != null) {
            emailField.clear();
        }
        // Vider les champs du formulaire de mot de passe
        if (currentPasswordField != null) {
            currentPasswordField.clear();
        }
        if (newPasswordField != null) {
            newPasswordField.clear();
        }
        if (confirmPasswordField != null) {
            confirmPasswordField.clear();
        }
    }

    @Override
    public void rafraichirProfil() {
        userProfilePresenter.chargerProfil();
    }

    @Override
    public Registration addAttachListener(ComponentEventListener<AttachEvent> listener) {
        return super.addAttachListener(listener);
    }
}
