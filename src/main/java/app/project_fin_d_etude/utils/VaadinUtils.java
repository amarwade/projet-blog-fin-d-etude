package app.project_fin_d_etude.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilitaires pour la création de composants Vaadin réutilisables et stylés.
 */
public final class VaadinUtils {

    private static final Logger logger = LoggerFactory.getLogger(VaadinUtils.class);
    private static final int NOTIFICATION_DURATION = 3000;
    private static final String LOADING_TEXT = "Chargement en cours...";
    private static final String DEFAULT_SEPARATOR_COLOR = "lightgray";
    private static final String DEFAULT_SEPARATOR_HEIGHT = "2px";

    private VaadinUtils() {
        // Classe utilitaire, constructeur privé
    }

    // =================== Notifications ===================
    /**
     * Affiche une notification avec un variant spécifique.
     *
     * @param message Le message à afficher
     * @param variant Le variant de la notification
     */
    public static void showNotification(String message, NotificationVariant variant) {
        if (message == null || message.trim().isEmpty()) {
            logger.warn("Tentative d'affichage d'une notification avec un message vide");
            return;
        }

        Notification notification = Notification.show(message, NOTIFICATION_DURATION, Notification.Position.TOP_CENTER);
        if (variant != null) {
            notification.addThemeVariants(variant);
        }
    }

    /**
     * Affiche une notification de succès.
     *
     * @param message Le message de succès
     */
    public static void showSuccessNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Affiche une notification d'erreur.
     *
     * @param message Le message d'erreur
     */
    public static void showErrorNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_ERROR);
    }

    /**
     * Affiche une notification d'avertissement.
     *
     * @param message Le message d'avertissement
     */
    public static void showWarningNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_WARNING);
    }

    /**
     * Affiche une notification d'information.
     *
     * @param message Le message d'information
     */
    public static void showInfoNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_PRIMARY);
    }

    // =================== Boutons ===================
    /**
     * Crée un bouton principal stylé.
     *
     * @param text Le texte du bouton
     * @return Le bouton créé
     */
    public static Button createPrimaryButton(String text) {
        if (text == null) {
            text = "";
        }

        Button button = new Button(text);
        button.addClassNames(
                LumoUtility.Background.PRIMARY,
                LumoUtility.TextColor.PRIMARY_CONTRAST,
                LumoUtility.Padding.Horizontal.LARGE,
                LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.FontWeight.MEDIUM,
                "animate__animated",
                "animate__pulse",
                "animate__infinite"
        );
        return button;
    }

    /**
     * Crée un bouton secondaire stylé.
     *
     * @param text Le texte du bouton
     * @return Le bouton créé
     */
    public static Button createSecondaryButton(String text) {
        if (text == null) {
            text = "";
        }

        Button button = new Button(text);
        button.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST,
                "animate__animated",
                "animate__fadeIn"
        );
        return button;
    }

    /**
     * Crée un bouton de danger stylé.
     *
     * @param text Le texte du bouton
     * @return Le bouton créé
     */
    public static Button createDangerButton(String text) {
        if (text == null) {
            text = "";
        }

        Button button = new Button(text);
        button.addClassNames(
                LumoUtility.Background.ERROR,
                LumoUtility.TextColor.ERROR_CONTRAST,
                LumoUtility.Padding.Horizontal.MEDIUM,
                LumoUtility.Padding.Vertical.SMALL,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.FontWeight.MEDIUM
        );
        return button;
    }

    // =================== Titres ===================
    /**
     * Crée un titre de page principal.
     *
     * @param text Le texte du titre
     * @return Le titre créé
     */
    public static H3 createPageTitle(String text) {
        if (text == null) {
            text = "";
        }

        H3 title = new H3(text);
        title.addClassNames(
                LumoUtility.FontSize.XXXLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.TextAlignment.CENTER,
                LumoUtility.Margin.Bottom.LARGE,
                LumoUtility.FontWeight.BOLD
        );
        return title;
    }

    /**
     * Crée un titre de section.
     *
     * @param text Le texte du titre
     * @return Le titre créé
     */
    public static H2 createSectionTitle(String text) {
        if (text == null) {
            text = "";
        }

        H2 title = new H2(text);
        title.addClassNames(
                LumoUtility.FontSize.XLARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Border.BOTTOM,
                LumoUtility.BorderColor.PRIMARY_50,
                LumoUtility.Padding.Bottom.SMALL,
                LumoUtility.Margin.Bottom.MEDIUM
        );
        return title;
    }

    /**
     * Crée un sous-titre.
     *
     * @param text Le texte du sous-titre
     * @return Le sous-titre créé
     */
    public static H3 createSubTitle(String text) {
        if (text == null) {
            text = "";
        }

        H3 title = new H3(text);
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontWeight.MEDIUM
        );
        return title;
    }

    // =================== Layouts ===================
    /**
     * Crée un séparateur horizontal.
     *
     * @param width La largeur du séparateur
     * @return Le séparateur créé
     */
    public static HorizontalLayout createSeparator(String width) {
        HorizontalLayout separator = new HorizontalLayout();
        separator.setWidth(width != null ? width : "100%");
        separator.setHeight(DEFAULT_SEPARATOR_HEIGHT);
        separator.getStyle().set("background-color", DEFAULT_SEPARATOR_COLOR);
        return separator;
    }

    /**
     * Crée un séparateur horizontal avec une couleur personnalisée.
     *
     * @param width La largeur du séparateur
     * @param color La couleur du séparateur
     * @return Le séparateur créé
     */
    public static HorizontalLayout createSeparator(String width, String color) {
        HorizontalLayout separator = new HorizontalLayout();
        separator.setWidth(width != null ? width : "100%");
        separator.setHeight(DEFAULT_SEPARATOR_HEIGHT);
        separator.getStyle().set("background-color", color != null ? color : DEFAULT_SEPARATOR_COLOR);
        return separator;
    }

    /**
     * Crée une section avec des propriétés personnalisées.
     *
     * @param width La largeur de la section
     * @param alignment L'alignement des éléments
     * @return La section créée
     */
    public static VerticalLayout createSection(String width, FlexComponent.Alignment alignment) {
        VerticalLayout section = new VerticalLayout();
        section.setWidth(width != null ? width : "100%");
        section.setPadding(true);
        section.setAlignItems(alignment != null ? alignment : FlexComponent.Alignment.START);
        section.addClassNames(
                LumoUtility.Margin.AUTO,
                LumoUtility.Background.CONTRAST_10,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.SMALL,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST
        );
        return section;
    }

    /**
     * Crée une section de formulaire.
     *
     * @return La section de formulaire créée
     */
    public static VerticalLayout createFormSection() {
        VerticalLayout section = new VerticalLayout();
        section.setWidth("50%");
        section.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST
        );
        section.setSpacing(true);
        return section;
    }

    /**
     * Crée une section de formulaire avec une largeur personnalisée.
     *
     * @param width La largeur de la section
     * @return La section de formulaire créée
     */
    public static VerticalLayout createFormSection(String width) {
        VerticalLayout section = new VerticalLayout();
        section.setWidth(width != null ? width : "50%");
        section.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Border.ALL,
                LumoUtility.BorderColor.CONTRAST
        );
        section.setSpacing(true);
        return section;
    }

    // =================== Champs ===================
    /**
     * Crée un champ de texte.
     *
     * @param label Le label du champ
     * @param placeholder Le placeholder du champ
     * @return Le champ de texte créé
     */
    public static TextField createTextField(String label, String placeholder) {
        TextField field = new TextField(label);
        field.setPlaceholder(placeholder != null ? placeholder : "");
        field.setWidthFull();
        field.setRequired(true);
        field.setRequiredIndicatorVisible(true);
        field.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextColor.PRIMARY
        );
        return field;
    }

    /**
     * Crée un champ de texte non requis.
     *
     * @param label Le label du champ
     * @param placeholder Le placeholder du champ
     * @return Le champ de texte créé
     */
    public static TextField createOptionalTextField(String label, String placeholder) {
        TextField field = new TextField(label);
        field.setPlaceholder(placeholder != null ? placeholder : "");
        field.setWidthFull();
        field.setRequired(false);
        field.setRequiredIndicatorVisible(false);
        field.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextColor.PRIMARY
        );
        return field;
    }

    /**
     * Crée un champ de texte multiligne.
     *
     * @param label Le label du champ
     * @param placeholder Le placeholder du champ
     * @param height La hauteur du champ
     * @return Le champ de texte multiligne créé
     */
    public static TextArea createTextArea(String label, String placeholder, String height) {
        TextArea area = new TextArea(label);
        area.setPlaceholder(placeholder != null ? placeholder : "");
        area.setWidthFull();
        area.setHeight(height != null ? height : "150px");
        area.setRequired(true);
        area.setRequiredIndicatorVisible(true);
        area.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextColor.PRIMARY
        );
        return area;
    }

    /**
     * Crée un champ de texte multiligne non requis.
     *
     * @param label Le label du champ
     * @param placeholder Le placeholder du champ
     * @param height La hauteur du champ
     * @return Le champ de texte multiligne créé
     */
    public static TextArea createOptionalTextArea(String label, String placeholder, String height) {
        TextArea area = new TextArea(label);
        area.setPlaceholder(placeholder != null ? placeholder : "");
        area.setWidthFull();
        area.setHeight(height != null ? height : "150px");
        area.setRequired(false);
        area.setRequiredIndicatorVisible(false);
        area.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextColor.PRIMARY
        );
        return area;
    }

    // =================== Dialogues ===================
    /**
     * Affiche un dialogue de confirmation.
     *
     * @param title Le titre du dialogue
     * @param message Le message du dialogue
     * @param onConfirm Le callback de confirmation
     */
    public static void showConfirmationDialog(String title, String message, SerializableConsumer<Boolean> onConfirm) {
        if (title == null) {
            title = "Confirmation";
        }
        if (message == null) {
            message = "Êtes-vous sûr de vouloir continuer ?";
        }
        if (onConfirm == null) {
            logger.warn("Callback de confirmation null, dialogue ignoré");
            return;
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.add(message);

        Button confirmButton = createPrimaryButton("Confirmer");
        confirmButton.addClickListener(e -> {
            dialog.close();
            onConfirm.accept(true);
        });

        Button cancelButton = createSecondaryButton("Annuler");
        cancelButton.addClickListener(e -> {
            dialog.close();
            onConfirm.accept(false);
        });

        dialog.getFooter().add(cancelButton, confirmButton);
        dialog.open();
    }

    /**
     * Affiche un dialogue d'erreur.
     *
     * @param title Le titre du dialogue
     * @param message Le message d'erreur
     */
    public static void showErrorDialog(String title, String message) {
        if (title == null) {
            title = "Erreur";
        }
        if (message == null) {
            message = "Une erreur s'est produite.";
        }

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(title);
        dialog.add(message);

        Button closeButton = createSecondaryButton("Fermer");
        closeButton.addClickListener(e -> dialog.close());

        dialog.getFooter().add(closeButton);
        dialog.open();
    }

    // =================== Responsive et Loading ===================
    /**
     * Ajoute des classes CSS pour l'animation responsive.
     *
     * @param component Le composant à animer
     */
    public static void addResponsiveClass(Component component) {
        if (component == null) {
            return;
        }

        component.addClassNames(
                "animate__animated",
                "animate__fadeIn",
                "animate__faster"
        );
    }

    /**
     * Crée un overlay de chargement.
     *
     * @param loadingText Le texte de chargement
     * @return L'overlay de chargement créé
     */
    public static Div createLoadingOverlay(String loadingText) {
        Div overlay = new Div();
        overlay.addClassNames("loading-overlay", "animate__animated", "animate__fadeIn");

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.addClassNames("loading-progress");

        Div loadingTextDiv = new Div();
        loadingTextDiv.setText(loadingText != null ? loadingText : LOADING_TEXT);
        loadingTextDiv.addClassNames("loading-text");

        overlay.add(progressBar, loadingTextDiv);
        return overlay;
    }

    /**
     * Crée un overlay de chargement avec le texte par défaut.
     *
     * @return L'overlay de chargement créé
     */
    public static Div createLoadingOverlay() {
        return createLoadingOverlay(LOADING_TEXT);
    }

    /**
     * Affiche un loader sur un composant parent.
     *
     * @param parent Le composant parent
     */
    public static void showLoading(Component parent) {
        if (parent == null) {
            return;
        }

        Div overlay = createLoadingOverlay();
        if (parent instanceof Div) {
            ((Div) parent).add(overlay);
        }
    }

    /**
     * Masque le loader d'un composant parent.
     *
     * @param parent Le composant parent
     */
    public static void hideLoading(Component parent) {
        if (parent == null) {
            return;
        }

        parent.getChildren()
                .filter(component -> component instanceof Div
                && ((Div) component).getClassNames().contains("loading-overlay"))
                .findFirst()
                .ifPresent(component -> ((Div) parent).remove(component));
    }
}
