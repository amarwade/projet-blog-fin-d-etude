package app.project_fin_d_etude;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.beans.factory.annotation.Autowired;

// @Theme(value = "project-fin-d-etude") // Supprimé, déplacé dans AppShellConfig
import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.PostRepository;

@SpringBootApplication
@EntityScan(basePackages = "app.project_fin_d_etude.model")
@EnableAsync
public class ProjectFinDEtudeApplication {

    private static final Logger logger = LoggerFactory.getLogger(ProjectFinDEtudeApplication.class);

    @Autowired
    private PostRepository postRepository;

    public static void main(String[] args) {
        SpringApplication.run(ProjectFinDEtudeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testDatabaseConnection() {
        logger.info("=== Test de connexion à la base de données ===");
        try {
            long count = postRepository.count();
            logger.info("Connexion à la base de données réussie. Nombre total de posts: {}", count);

            if (count > 0) {
                logger.info("Récupération du premier post pour test...");
                Optional<Post> firstPost = postRepository.findAll().stream().findFirst();
                if (firstPost.isPresent()) {
                    logger.info("Premier post trouvé: ID={}, Titre={}",
                            firstPost.get().getId(), firstPost.get().getTitre());
                }
            } else {
                logger.warn("Aucun post trouvé dans la base de données - création de données de test");
                createTestData(postRepository);
            }
        } catch (Exception e) {
            logger.error("Erreur lors du test de connexion à la base de données: {}", e.getMessage(), e);
        }
        logger.info("=== Fin du test de connexion ===");
    }

    private void createTestData(PostRepository postRepository) {
        try {
            Post testPost1 = new Post();
            testPost1.setTitre("Premier article de test");
            testPost1.setContenu("Ceci est le contenu du premier article de test. Il contient du texte pour tester l'affichage des articles sur la page d'accueil.");
            testPost1.setAuteurNom("Admin Test");
            testPost1.setAuteurEmail("admin@test.com");
            testPost1.setDatePublication(java.time.LocalDateTime.now().minusDays(1));

            Post testPost2 = new Post();
            testPost2.setTitre("Deuxième article de test");
            testPost2.setContenu("Ceci est le contenu du deuxième article de test. Il permet de vérifier que plusieurs articles s'affichent correctement.");
            testPost2.setAuteurNom("Utilisateur Test");
            testPost2.setAuteurEmail("user@test.com");
            testPost2.setDatePublication(java.time.LocalDateTime.now().minusHours(6));

            postRepository.save(testPost1);
            postRepository.save(testPost2);

            logger.info("Données de test créées avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la création des données de test: {}", e.getMessage(), e);
        }
    }
}
