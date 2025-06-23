package app.project_fin_d_etude.views;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import app.project_fin_d_etude.layout.MainLayout;
import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.presenter.MessagePresenter;
import app.project_fin_d_etude.utils.VaadinUtils;
import app.project_fin_d_etude.utils.ValidationUtils;
import app.project_fin_d_etude.utils.ExceptionHandler;

/**
 * Vue de contact permettant aux utilisateurs d'envoyer des messages. Cette vue
 * est accessible via la route "/contact".
 */
@Route(value = "contact", layout = MainLayout.class)
@PageTitle("Contact")
public class ContactView extends VerticalLayout {

    private static final String SUCCESS_MESSAGE = "Message envoyé avec succès !";

    private final MessagePresenter messagePresenter;
    private TextField nameField;
    private TextField emailField;
    private TextField subjectField;
    private TextArea messageArea;

    @Autowired
    public ContactView(MessagePresenter messagePresenter) {
        this.messagePresenter = messagePresenter;
        configureLayout();
        add(createMainContent());
    }

    private void configureLayout() {
        setSpacing(false);
        setPadding(false);
        setSizeFull();
        addClassNames(LumoUtility.Background.CONTRAST_5);
    }

    private VerticalLayout createMainContent() {
        VerticalLayout mainContent = VaadinUtils.createSection("100%", FlexComponent.Alignment.CENTER);

        // Premier séparateur (au-dessus du titre)
        mainContent.add(VaadinUtils.createSeparator("70%"));

        mainContent.add(VaadinUtils.createPageTitle("CONTACT"));

        // Deuxième séparateur (en-dessous du titre)
        mainContent.add(VaadinUtils.createSeparator("70%"));

        mainContent.add(createPersonalInfo());
        mainContent.add(createContactForm());
        return mainContent;
    }

    private VerticalLayout createPersonalInfo() {
        VerticalLayout personalInfo = new VerticalLayout();
        personalInfo.setWidth("50%");
        personalInfo.addClassNames(
                LumoUtility.Background.CONTRAST_5,
                LumoUtility.Padding.LARGE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Margin.Bottom.LARGE
        );
        personalInfo.setSpacing(true);
        personalInfo.setAlignItems(FlexComponent.Alignment.CENTER);

        return personalInfo;
    }

    private VerticalLayout createContactForm() {
        VerticalLayout formLayout = VaadinUtils.createFormSection();

        initializeFormFields();
        formLayout.add(nameField, emailField, subjectField, messageArea);
        formLayout.add(createSendButton());

        return formLayout;
    }

    private void initializeFormFields() {
        nameField = VaadinUtils.createTextField("Nom", "Votre nom");
        emailField = VaadinUtils.createTextField("Email", "Votre email");
        subjectField = VaadinUtils.createTextField("Sujet", "Sujet de votre message");
        messageArea = VaadinUtils.createTextArea("Message", "Votre message", "200px");
    }

    private Button createSendButton() {
        Button sendButton = VaadinUtils.createPrimaryButton("ENVOYER");
        sendButton.addClassNames(LumoUtility.Margin.Top.LARGE, LumoUtility.Margin.AUTO);
        sendButton.addClickListener(e -> envoyerMessage());
        return sendButton;
    }

    private void envoyerMessage() {
        if (!validerFormulaire()) {
            return;
        }

        Message message = new Message();
        message.setNom(nameField.getValue().trim());
        message.setEmail(emailField.getValue().trim());
        message.setSujet(subjectField.getValue().trim());
        message.setContenu(messageArea.getValue().trim());

        ExceptionHandler.executeWithErrorHandling(
                () -> {
                    messagePresenter.envoyerMessage(message);
                    VaadinUtils.showSuccessNotification(SUCCESS_MESSAGE);
                    viderFormulaire();
                },
                "envoi de message",
                errorMessage -> VaadinUtils.showErrorNotification(errorMessage)
        );
    }

    private boolean validerFormulaire() {
        // Validation du nom
        ValidationUtils.ValidationResult nameResult = ValidationUtils.validateName(nameField);
        if (!nameResult.isValid()) {
            VaadinUtils.showErrorNotification(nameResult.getErrorMessage());
            return false;
        }

        // Validation de l'email
        ValidationUtils.ValidationResult emailResult = ValidationUtils.validateEmail(emailField);
        if (!emailResult.isValid()) {
            VaadinUtils.showErrorNotification(emailResult.getErrorMessage());
            return false;
        }

        // Validation du sujet
        ValidationUtils.ValidationResult subjectResult = ValidationUtils.validateSubject(subjectField);
        if (!subjectResult.isValid()) {
            VaadinUtils.showErrorNotification(subjectResult.getErrorMessage());
            return false;
        }

        // Validation du message
        ValidationUtils.ValidationResult messageResult = ValidationUtils.validateContent(messageArea);
        if (!messageResult.isValid()) {
            VaadinUtils.showErrorNotification(messageResult.getErrorMessage());
            return false;
        }

        return true;
    }

    private void viderFormulaire() {
        getUI().ifPresent(ui -> ui.access(() -> {
            nameField.clear();
            emailField.clear();
            subjectField.clear();
            messageArea.clear();
        }));
    }
}
