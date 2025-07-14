package app.project_fin_d_etude.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import app.project_fin_d_etude.model.Message;
import app.project_fin_d_etude.repository.MessageRepository;
import app.project_fin_d_etude.utils.EntityValidator;

@Service
public class MessageService {

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

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
        logger.info("Message sauvegardé avec succès - id: {}, sujet: {}", saved.getId(), saved.getSujet());
        return saved;
    }

    /**
     * Récupère tous les messages triés par date d'envoi décroissante, de façon
     * asynchrone.
     */
    @Async
    public CompletableFuture<List<Message>> getAllMessages() {
        try {
            List<Message> messages = messageRepository.findAllByOrderByDateEnvoiDesc();
            logger.debug("Récupération asynchrone de {} messages", messages.size());
            return CompletableFuture.completedFuture(messages);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération asynchrone de tous les messages: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * Récupère les messages non lus, triés par date d'envoi décroissante, de
     * façon asynchrone.
     */
    @Async
    public CompletableFuture<List<Message>> getUnreadMessages() {
        try {
            List<Message> messages = messageRepository.findByLuOrderByDateEnvoiDesc(false);
            logger.debug("Récupération asynchrone de {} messages non lus", messages.size());
            return CompletableFuture.completedFuture(messages);
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération asynchrone des messages non lus: {}", e.getMessage(), e);
            return CompletableFuture.failedFuture(e);
        }
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
