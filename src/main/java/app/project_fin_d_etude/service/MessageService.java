package app.project_fin_d_etude.service;

import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.repository.MessageRepository;
import app.project_fin_d_etude.utils.EntityValidator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    /**
     * Sauvegarde un message après validation, de façon asynchrone.
     *
     * @param message Le message à sauvegarder
     * @return Future contenant le message sauvegardé
     */
    @Async
    public CompletableFuture<Message> save(Message message) {
        EntityValidator.ValidationResult validationResult = EntityValidator.validateMessage(message);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Message invalide: " + validationResult.getAllErrorsAsString());
        }
        return CompletableFuture.completedFuture(messageRepository.save(message));
    }

    /**
     * Récupère tous les messages triés par date d'envoi décroissante, de façon
     * asynchrone.
     *
     * @return Future contenant la liste des messages
     */
    @Async
    public CompletableFuture<List<Message>> getAllMessages() {
        return CompletableFuture.completedFuture(messageRepository.findAllByOrderByDateEnvoiDesc());
    }

    /**
     * Récupère les messages non lus, triés par date d'envoi décroissante, de
     * façon asynchrone.
     *
     * @return Future contenant la liste des messages non lus
     */
    @Async
    public CompletableFuture<List<Message>> getUnreadMessages() {
        return CompletableFuture.completedFuture(messageRepository.findByLuOrderByDateEnvoiDesc(false));
    }

    /**
     * Marque un message comme lu par son identifiant, de façon asynchrone.
     *
     * @param messageId L'identifiant du message
     * @return Future complétée une fois la mise à jour effectuée
     */
    @Async
    public CompletableFuture<Void> markAsRead(Long messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("L'ID du message ne peut pas être null");
        }
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setLu(true);
            messageRepository.save(message);
        });
        return CompletableFuture.completedFuture(null);
    }

    /**
     * Supprime un message par son identifiant, de façon asynchrone.
     *
     * @param messageId L'identifiant du message
     * @return Future complétée une fois la suppression effectuée
     */
    @Async
    public CompletableFuture<Void> delete(Long messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("L'ID du message ne peut pas être null");
        }
        messageRepository.deleteById(messageId);
        return CompletableFuture.completedFuture(null);
    }
}
