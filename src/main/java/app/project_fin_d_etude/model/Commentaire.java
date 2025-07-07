package app.project_fin_d_etude.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "commentaire")
public class Commentaire implements Serializable {

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
    private LocalDateTime dateCreation;

    @PrePersist
    public void prePersist() {
        if (dateCreation == null) {
            dateCreation = LocalDateTime.now();
        }
    }

    /**
     * Email de l'auteur du commentaire (récupéré via Keycloak).
     */
    @Column(nullable = false, length = 200)
    private String auteurEmail;

    /**
     * Nom complet de l'auteur du commentaire (récupéré via Keycloak).
     */
    @Column(nullable = false, length = 200)
    private String auteurNom;

    /**
     * Article auquel le commentaire est associé.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    /**
     * Indique si le commentaire est inapproprié.
     */
    @Column(nullable = false)
    private boolean inapproprie = false;

    @ManyToOne
    private Commentaire parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> replies;
}
