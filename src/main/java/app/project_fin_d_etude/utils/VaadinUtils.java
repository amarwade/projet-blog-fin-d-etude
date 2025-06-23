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
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.SerializableConsumer;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Utilitaires pour la création de composants Vaadin réutilisables et stylés.
 */
public class VaadinUtils {

    private static final int NOTIFICATION_DURATION = 3000;
    private static final String LOADING_TEXT = "Chargement en cours...";

    // =================== Notifications ===================
    /**
     * Affiche une notification personnalisée.
     */
    public static void showNotification(String message, NotificationVariant variant) {
        Notification notification = Notification.show(message, NOTIFICATION_DURATION, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(variant);
    }

    /**
     * Affiche une notification de succès.
     */
    public static void showSuccessNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_SUCCESS);
    }

    /**
     * Affiche une notification d'erreur.
     */
    public static void showErrorNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_ERROR);
    }

    /**
     * Affiche une notification d'avertissement.
     */
    public static void showWarningNotification(String message) {
        showNotification(message, NotificationVariant.LUMO_WARNING);
    }

    // =================== Boutons ===================
    /**
     * Crée un bouton principal stylé.
     */
    public static Button createPrimaryButton(String text) {
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
     */
    public static Button createSecondaryButton(String text) {
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

    // =================== Titres ===================
    /**
     * Crée un titre de page (H1) stylé.
     */
    public static H1 createPageTitle(String text) {
        H1 title = new H1(text);
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
     * Crée un titre de section (H2) stylé.
     */
    public static H2 createSectionTitle(String text) {
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
     * Crée un sous-titre (H3) stylé.
     */
    public static H3 createSubTitle(String text) {
        H3 title = new H3(text);
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.TextColor.PRIMARY,
                LumoUtility.Margin.Bottom.MEDIUM,
                LumoUtility.FontWeight.MEDIUM
        );
        return title;
    }

    // =================== Layouts et sections ===================
    /**
     * Crée un séparateur horizontal stylé.
     */
    public static HorizontalLayout createSeparator(String width) {
        HorizontalLayout separator = new HorizontalLayout();
        separator.setWidth(width);
        separator.setHeight("2px");
        separator.getStyle().set("background-color", "lightgray");
        separator.addClassNames("separator");
        return separator;
    }

    /**
     * Crée une section verticale stylée.
     */
    public static VerticalLayout createSection(String width, FlexComponent.Alignment alignment) {
        VerticalLayout section = new VerticalLayout();
        section.setWidth(width);
        section.setPadding(true);
        section.setAlignItems(alignment);
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
     * Crée une section de formulaire stylée.
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

    // =================== Champs de formulaire ===================
    /**
     * Crée un champ texte stylé.
     */
    public static TextField createTextField(String label, String placeholder) {
        TextField field = new TextField(label);
        field.setPlaceholder(placeholder);
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
     * Crée une zone de texte stylée.
     */
    public static TextArea createTextArea(String label, String placeholder, String height) {
        TextArea area = new TextArea(label);
        area.setPlaceholder(placeholder);
        area.setWidthFull();
        area.setHeight(height);
        area.setRequired(true);
        area.setRequiredIndicatorVisible(true);
        area.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.TextColor.PRIMARY
        );
        return area;
    }

    // =================== Dialogues et overlays ===================
    /**
     * Affiche une boîte de dialogue de confirmation avec callback.
     */
    public static void showConfirmationDialog(String title, String message, SerializableConsumer<Boolean> onConfirm) {
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
     * Ajoute des classes CSS responsives à un composant.
     */
    public static void addResponsiveClass(Component component) {
        component.addClassNames(
                "animate__animated",
                "animate__fadeIn",
                "animate__faster"
        );
    }

    // =================== Loading ===================
    /**
     * Crée un overlay de chargement stylé.
     */
    public static Div createLoadingOverlay() {
        Div overlay = new Div();
        overlay.addClassNames(
                "loading-overlay",
                "animate__animated",
                "animate__fadeIn"
        );

        ProgressBar progressBar = new ProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.addClassNames("loading-progress");

        Div loadingText = new Div();
        loadingText.setText(LOADING_TEXT);
        loadingText.addClassNames("loading-text");

        overlay.add(progressBar, loadingText);
        return overlay;
    }

    /**
     * Affiche un overlay de chargement sur un composant parent.
     */
    public static void showLoading(Component parent) {
        Div overlay = createLoadingOverlay();
        if (parent instanceof Div) {
            ((Div) parent).add(overlay);
        }
    }

    /**
     * Masque l'overlay de chargement sur un composant parent.
     */
    public static void hideLoading(Component parent) {
        parent.getChildren()
                .filter(component -> component instanceof Div
                && ((Div) component).getClassNames().contains("loading-overlay"))
                .findFirst()
                .ifPresent(component -> ((Div) parent).remove(component));
    }
}
