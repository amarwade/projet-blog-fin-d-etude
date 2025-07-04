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
     * Sauvegarde un message après validation, de façon synchrone.
     */
    public Message save(Message message) {
        EntityValidator.ValidationResult validationResult = EntityValidator.validateMessage(message);
        if (!validationResult.isValid()) {
            throw new IllegalArgumentException("Message invalide: " + validationResult.getAllErrorsAsString());
        }
        Message saved = messageRepository.save(message);
        System.out.println(">>> Message sauvegardé : " + saved);
        return saved;
    }

    /**
     * Récupère tous les messages triés par date d'envoi décroissante, de façon asynchrone.
     */
    @Async
    public CompletableFuture<List<Message>> getAllMessages() {
        return CompletableFuture.completedFuture(messageRepository.findAllByOrderByDateEnvoiDesc());
    }

    /**
     * Récupère les messages non lus, triés par date d'envoi décroissante, de façon asynchrone.
     */
    @Async
    public CompletableFuture<List<Message>> getUnreadMessages() {
        return CompletableFuture.completedFuture(messageRepository.findByLuOrderByDateEnvoiDesc(false));
    }

    /**
     * Marque un message comme lu par son identifiant, de façon synchrone.
     */
    public void markAsRead(Long messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("L'ID du message ne peut pas être null");
        }
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setLu(true);
            messageRepository.save(message);
        });
    }

    /**
     * Supprime un message par son identifiant, de façon synchrone.
     */
    public void delete(Long messageId) {
        if (messageId == null) {
            throw new IllegalArgumentException("L'ID du message ne peut pas être null");
        }
        messageRepository.deleteById(messageId);
    }
}
