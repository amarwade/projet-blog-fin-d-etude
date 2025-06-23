package app.project_fin_d_etude.presenter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;

import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.service.MessageService;
import lombok.Setter;

@Component
public class MessagePresenter {

    @Setter
    private MessageView view;
    private final MessageService messageService;

    /**
     * Interface à implémenter par la vue pour lier le présentateur.
     */
    public interface MessageView {

        void afficherMessages(List<Message> messages);

        void afficherMessage(String message);

        void afficherErreur(String erreur);
    }

    public MessagePresenter(MessageService messageService) {
        this.messageService = messageService;
    }

    /**
     * Envoie un message et notifie la vue du succès ou de l'échec.
     */
    public void envoyerMessage(Message message) {
        if (view == null) {
            return;
        }
        final MessageView currentView = this.view;
        final UI ui = UI.getCurrent();
        messageService.save(message)
                .whenComplete((savedMessage, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors de l'envoi du message : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Message envoyé avec succès !");
                        }
                    });
                });
    }

    /**
     * Charge tous les messages et les transmet à la vue.
     */
    public void chargerMessages() {
        if (view == null) {
            return;
        }
        final MessageView currentView = this.view;
        final UI ui = UI.getCurrent();
        messageService.getAllMessages()
                .whenComplete((messages, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors du chargement des messages : " + ex.getMessage());
                        } else {
                            currentView.afficherMessages(messages);
                        }
                    });
                });
    }

    /**
     * Marque un message comme lu et notifie la vue.
     */
    public void marquerCommeLu(Long messageId) {
        if (view == null) {
            return;
        }
        final MessageView currentView = this.view;
        final UI ui = UI.getCurrent();
        messageService.markAsRead(messageId)
                .whenComplete((unused, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors du marquage du message : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Message marqué comme lu");
                        }
                    });
                });
    }

    /**
     * Supprime un message et notifie la vue.
     */
    public void supprimerMessage(Long messageId) {
        if (view == null) {
            return;
        }
        final MessageView currentView = this.view;
        final UI ui = UI.getCurrent();
        messageService.delete(messageId)
                .whenComplete((unused, ex) -> {
                    ui.access(() -> {
                        if (ex != null) {
                            currentView.afficherErreur("Erreur lors de la suppression du message : " + ex.getMessage());
                        } else {
                            currentView.afficherMessage("Message supprimé avec succès");
                        }
                    });
                });
    }
}
