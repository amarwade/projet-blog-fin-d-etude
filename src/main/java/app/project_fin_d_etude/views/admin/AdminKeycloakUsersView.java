package app.project_fin_d_etude.views.admin;

import app.project_fin_d_etude.layout.AdminLayout;
import app.project_fin_d_etude.service.KeycloakUserAdminService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import app.project_fin_d_etude.utils.VaadinUtils;

/**
 * Vue d'administration des utilisateurs Keycloak : CRUD complet.
 */
@Route(value = "admin/keycloak-users", layout = AdminLayout.class)
@PageTitle("Gestion des utilisateurs Keycloak - Administration")
@RolesAllowed("ADMIN")
public class AdminKeycloakUsersView extends VerticalLayout {

    private final KeycloakUserAdminService keycloakUserAdminService;
    private final Grid<UserRepresentation> grid = new Grid<>(UserRepresentation.class, false);
    private final Paragraph noUsersMessage = new Paragraph("Aucun utilisateur Keycloak à afficher.");

    @Autowired
    public AdminKeycloakUsersView(KeycloakUserAdminService keycloakUserAdminService) {
        this.keycloakUserAdminService = keycloakUserAdminService;
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);

        add(createMainContent());
        configureGrid();
        loadUsers();
    }

    /**
     * Crée le layout principal (titre, section, grid, bouton).
     */
    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = new VerticalLayout();
        mainContent.setWidth("100%");
        mainContent.setPadding(true);
        mainContent.setAlignItems(Alignment.CENTER);
        mainContent.addClassNames(
                LumoUtility.Margin.AUTO,
                LumoUtility.Background.CONTRAST_10,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL
        );

        H1 pageTitle = new H1("GESTION DES UTILISATEURS KEYCLOAK");
        pageTitle.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        mainContent.add(pageTitle);

        Button addButton = new Button("Ajouter un utilisateur", e -> openUserDialog(null));
        addButton.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
        mainContent.add(addButton);

        mainContent.add(createContentSection());
        return mainContent;
    }

    /**
     * Crée la section contenant la grille des utilisateurs.
     */
    private VerticalLayout createContentSection() {
        VerticalLayout contentSection = new VerticalLayout();
        contentSection.setWidth("90%");
        contentSection.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM
        );
        contentSection.add(grid);
        noUsersMessage.addClassNames(LumoUtility.TextColor.SECONDARY, LumoUtility.TextAlignment.CENTER);
        noUsersMessage.setVisible(false);
        contentSection.add(noUsersMessage);
        return contentSection;
    }

    /**
     * Configure la grille d'affichage des utilisateurs Keycloak.
     */
    private void configureGrid() {
        grid.addColumn(UserRepresentation::getUsername).setHeader("Nom d'utilisateur").setAutoWidth(true);
        grid.addColumn(UserRepresentation::getEmail).setHeader("Email").setAutoWidth(true);
        grid.addColumn(user -> user.isEnabled() ? "Actif" : "Inactif").setHeader("Statut").setAutoWidth(true);
        grid.addColumn(user -> "-").setHeader("Rôle").setAutoWidth(true);
        grid.addComponentColumn(this::createActionsColumn).setHeader("Actions");
    }

    /**
     * Charge et affiche les utilisateurs Keycloak.
     */
    private void loadUsers() {
        VaadinUtils.showLoading(this);
        final UI ui = UI.getCurrent();
        keycloakUserAdminService.listAllUsers().whenComplete((users, ex) -> {
            ui.access(() -> {
                VaadinUtils.hideLoading(this);
                if (ex != null) {
                    Notification.show("Erreur lors du chargement des utilisateurs : " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                    grid.setItems(List.of());
                    noUsersMessage.setVisible(true);
                } else {
                    if (users == null || users.isEmpty()) {
                        grid.setItems(List.of());
                        noUsersMessage.setVisible(true);
                    } else {
                        grid.setItems(users);
                        noUsersMessage.setVisible(false);
                    }
                }
            });
        });
    }

    /**
     * Colonne d'actions (modifier, supprimer).
     */
    private HorizontalLayout createActionsColumn(UserRepresentation user) {
        Button edit = new Button("Modifier", e -> openUserDialog(user));
        Button delete = new Button("Supprimer", e -> openDeleteDialog(user));
        edit.addClassNames(LumoUtility.Margin.Right.SMALL);
        delete.getStyle().set("color", "red");
        return new HorizontalLayout(edit, delete);
    }

    /**
     * Ouvre le dialogue d'ajout/modification d'utilisateur.
     */
    private void openUserDialog(UserRepresentation user) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        boolean isEdit = user != null;
        dialog.setHeaderTitle(isEdit ? "Modifier l'utilisateur" : "Ajouter un utilisateur");

        TextField usernameField = new TextField("Nom d'utilisateur");
        usernameField.setWidthFull();
        EmailField emailField = new EmailField("Email");
        emailField.setWidthFull();
        PasswordField passwordField = new PasswordField("Mot de passe");
        passwordField.setWidthFull();
        passwordField.setPlaceholder(isEdit ? "Laisser vide pour ne pas changer" : "");

        usernameField.setValue(isEdit ? user.getUsername() : "");
        emailField.setValue(isEdit ? user.getEmail() : "");
        passwordField.setValue("");

        FormLayout form = new FormLayout(usernameField, emailField, passwordField);
        form.setWidthFull();

        Button saveButton = new Button(isEdit ? "Enregistrer" : "Créer", e -> {
            if (usernameField.isEmpty() || emailField.isEmpty() || (!isEdit && passwordField.isEmpty())) {
                Notification.show("Tous les champs sont obligatoires (sauf mot de passe en modification)", 3000, Notification.Position.MIDDLE);
                return;
            }

            final UI ui = UI.getCurrent();
            CompletableFuture<Void> saveFuture;

            if (isEdit) {
                saveFuture = keycloakUserAdminService.updateUser(user.getId(), usernameField.getValue(), emailField.getValue(), user.isEnabled())
                        .thenCompose(v -> {
                            if (!passwordField.isEmpty()) {
                                return keycloakUserAdminService.updatePassword(user.getId(), passwordField.getValue());
                            }
                            return CompletableFuture.completedFuture(null);
                        });
            } else {
                saveFuture = keycloakUserAdminService.createUser(usernameField.getValue(), emailField.getValue(), passwordField.getValue(), true)
                        .thenAccept(userId -> {
                        }).thenApply(v -> null);
            }

            saveFuture.whenComplete((result, ex) -> {
                ui.access(() -> {
                    if (ex != null) {
                        Notification.show("Erreur : " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                    } else {
                        Notification.show("Opération réussie", 3000, Notification.Position.MIDDLE);
                        dialog.close();
                        loadUsers();
                    }
                });
            });
        });
        saveButton.addClassNames(LumoUtility.Margin.Top.MEDIUM);
        Button cancelButton = new Button("Annuler", e -> dialog.close());
        HorizontalLayout buttons = new HorizontalLayout(saveButton, cancelButton);
        dialog.add(form, buttons);
        dialog.open();
    }

    /**
     * Ouvre le dialogue de confirmation de suppression.
     */
    private void openDeleteDialog(UserRepresentation user) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Supprimer l'utilisateur");
        dialog.add(new Paragraph("Êtes-vous sûr de vouloir supprimer l'utilisateur " + user.getUsername() + " ?"));
        Button confirm = new Button("Confirmer", e -> {
            final UI ui = UI.getCurrent();
            keycloakUserAdminService.deleteUser(user.getId()).whenComplete((unused, ex) -> {
                ui.access(() -> {
                    if (ex != null) {
                        Notification.show("Erreur : " + ex.getMessage(), 4000, Notification.Position.MIDDLE);
                    } else {
                        Notification.show("Utilisateur supprimé", 3000, Notification.Position.MIDDLE);
                        dialog.close();
                        loadUsers();
                    }
                });
            });
        });
        Button cancel = new Button("Annuler", e -> dialog.close());
        confirm.getStyle().set("color", "red");
        dialog.add(new HorizontalLayout(confirm, cancel));
        dialog.open();
    }
}
