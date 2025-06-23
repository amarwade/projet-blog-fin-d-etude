package app.project_fin_d_etude.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.project_fin_d_etude.model.Message;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Récupère les messages selon leur statut de lecture, triés par date
     * d'envoi décroissante.
     *
     * @param lu true pour les messages lus, false pour les non lus
     * @return Liste des messages filtrés et triés
     */
    List<Message> findByLuOrderByDateEnvoiDesc(boolean lu);

    /**
     * Récupère tous les messages triés par date d'envoi décroissante.
     *
     * @return Liste de tous les messages triés
     */
    List<Message> findAllByOrderByDateEnvoiDesc();
}
