package app.project_fin_d_etude.model;

import java.time.LocalDateTime;
import java.util.List;
import java.io.Serializable;
import java.util.ArrayList;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "post")
public class Post implements Serializable {

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
     * Email de l'auteur de l'article (récupéré via Keycloak).
     */
    @Column(nullable = false, length = 200)
    private String auteurEmail;

    /**
     * Nom complet de l'auteur de l'article (récupéré via Keycloak).
     */
    @Column(nullable = false, length = 200)
    private String auteurNom;

    /**
     * Liste des commentaires liés à l'article.
     */
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();

    public Post(Long id, String titre, String contenu, LocalDateTime datePublication, String auteurEmail, String auteurNom, List<Commentaire> commentaires) {
        this.id = id;
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.auteurEmail = auteurEmail;
        this.auteurNom = auteurNom;
        this.commentaires = commentaires != null ? commentaires : new ArrayList<>();
    }

    public Post(String titre, String contenu, LocalDateTime datePublication, String auteurEmail, String auteurNom) {
        this.titre = titre;
        this.contenu = contenu;
        this.datePublication = datePublication;
        this.auteurEmail = auteurEmail;
        this.auteurNom = auteurNom;
    }
}
