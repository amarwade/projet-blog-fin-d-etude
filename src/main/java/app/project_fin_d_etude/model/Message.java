package app.project_fin_d_etude.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message {

    /**
     * Identifiant unique du message.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom de l'expéditeur du message.
     */
    @Column(nullable = false, length = 100)
    private String nom;

    /**
     * Email de l'expéditeur du message.
     */
    @Column(nullable = false, length = 100)
    private String email;

    /**
     * Sujet du message.
     */
    @Column(nullable = false, length = 200)
    private String sujet;

    /**
     * Contenu textuel du message.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    /**
     * Date d'envoi du message.
     */
    @Column(nullable = false)
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    /**
     * Statut de lecture du message (lu ou non).
     */
    @Column(nullable = false)
    private boolean lu = false;
}
