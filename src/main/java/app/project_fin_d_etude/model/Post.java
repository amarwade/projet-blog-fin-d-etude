package app.project_fin_d_etude.model;

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
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post {

    /**
     * Identifiant unique du post.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Titre de l'article.
     */
    @Column(nullable = false, length = 200)
    private String titre;

    /**
     * Contenu textuel de l'article.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    /**
     * Date de publication de l'article.
     */
    @Column(nullable = false)
    private LocalDateTime datePublication;

    /**
     * Auteur de l'article (utilisateur).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur auteur;

    /**
     * Liste des commentaires liés à l'article.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires;
}
