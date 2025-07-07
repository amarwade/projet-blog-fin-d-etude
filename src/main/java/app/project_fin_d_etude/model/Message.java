package app.project_fin_d_etude.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "message")
public class Message implements Serializable {

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
    private LocalDateTime dateEnvoi;

    /**
     * Statut de lecture du message (lu ou non).
     */
    @Column(nullable = false)
    private boolean lu = false;

    @PrePersist
    public void prePersist() {
        if (dateEnvoi == null) {
            dateEnvoi = LocalDateTime.now();
        }
    }
}
