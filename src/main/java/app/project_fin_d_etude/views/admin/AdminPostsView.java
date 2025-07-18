package app.project_fin_d_etude.views.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.AdminLayout;
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.presenter.PostPresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import org.springframework.security.access.annotation.Secured;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value = "admin/articles", layout = AdminLayout.class)

@PageTitle("Gestion des articles - Administration")
@AnonymousAllowed
public class AdminPostsView extends VerticalLayout implements PostPresenter.PostView {

    private final PostPresenter postPresenter;
    private final Grid<Post> grid = new Grid<>(Post.class);
    private final Paragraph noPostsMessage = new Paragraph("Aucun article à afficher.");

    @Autowired
    public AdminPostsView(PostPresenter postPresenter) {
        this.postPresenter = postPresenter;
        this.postPresenter.setView(this);

        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);

        add(createMainContent());
        configureGrid();
        VaadinUtils.showLoading(this);
        postPresenter.chargerPosts();

        System.out.println("Authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }

    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("100%");
        mainContent.setPadding(true);
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.addClassNames(
                LumoUtility.Margin.AUTO,
                LumoUtility.Background.CONTRAST_10,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );

        mainContent.add(createPageTitle());
        mainContent.add(createContentSection());

        return mainContent;
    }

    private H1 createPageTitle() {
        H1 pageTitle = new H1("GESTION DES POSTS");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        return pageTitle;
    }

    private VerticalLayout createContentSection() {
        VerticalLayout contentSection = new VerticalLayout();
        contentSection.setWidth("100%");
        contentSection.setHeight("600px");
        contentSection.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );

        contentSection.add(grid);
        noPostsMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);
        noPostsMessage.setVisible(false);
        contentSection.add(noPostsMessage);
        return contentSection;
    }

    private void configureGrid() {
        grid.addClassNames("contact-grid");
        grid.setColumns("id", "titre", "datePublication");

        grid.addColumn(post -> {
            if (post.getAuteurNom() != null) {
                return post.getAuteurNom();
            }
            return "Auteur inconnu";
        }).setHeader("Auteur");

        grid.addComponentColumn(post -> {
            String contenu = post.getContenu();
            String contenuAffiche = contenu != null && contenu.length() > 100 ? contenu.substring(0, 100) + "…" : contenu;
            Span contenuSpan = new Span(contenuAffiche);
            contenuSpan.addClassName("admin-posts-contenu");
            return contenuSpan;
        }).setHeader("Contenu").setWidth("120px").setFlexGrow(0);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        //un grid colum pour action où il y'aura de bouton qui vont permettre de lire plus de détails post
        grid.addComponentColumn(this::createActionsColumn).setHeader("Actions");
    }

    private HorizontalLayout createActionsColumn(Post post) {
        Button voirDetails = new Button("Détails", e
                -> getUI().ifPresent(ui -> ui.navigate("user/article/" + post.getId()))
        );
        voirDetails.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return new HorizontalLayout(voirDetails);
    }

    @Override
    public void afficherPosts(List<Post> posts) {
        getUI().ifPresent(ui -> ui.access(() -> {
            VaadinUtils.hideLoading(this);
            if (posts == null || posts.isEmpty()) {
                grid.setItems(List.of());
                noPostsMessage.setVisible(true);
            } else {
                grid.setItems(posts);
                noPostsMessage.setVisible(false);
            }
        }));
    }

    @Override
    public void afficherMessage(String message) {
        VaadinUtils.showSuccessNotification(message);
    }

    @Override
    public void afficherErreur(String erreur) {
        VaadinUtils.showErrorNotification(erreur);
    }

    @Override
    public void viderFormulaire() {
        // Non utilisé dans cette vue
    }

    @Override
    public void redirigerVersDetail(Long postId) {
        // Non utilisé dans cette vue
    }

    @Override
    public void afficherPost(Post post) {
        // Non utilisé dans cette vue
    }
}
