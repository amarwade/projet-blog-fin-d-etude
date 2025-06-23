package app.project_fin_d_etude.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "commentaire")
public class Commentaire {

    /**
     * Identifiant unique du commentaire.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contenu textuel du commentaire.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    /**
     * Date de création du commentaire.
     */
    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    /**
     * Auteur du commentaire (utilisateur).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur auteur;

    /**
     * Article auquel le commentaire est associé.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}
