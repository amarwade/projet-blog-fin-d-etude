package app.project_fin_d_etude.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.PostRepository;

@RestController
@RequestMapping("/api/diagnostic")
public class FallbackController {

    private static final Logger logger = LoggerFactory.getLogger(FallbackController.class);

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> databaseCheck() {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Test de connexion à la base de données");
            long count = postRepository.count();
            response.put("status", "SUCCESS");
            response.put("message", "Connexion à la base de données réussie");
            response.put("postCount", count);

            if (count > 0) {
                List<Post> posts = postRepository.findAll();
                response.put("samplePost", Map.of(
                        "id", posts.get(0).getId(),
                        "titre", posts.get(0).getTitre(),
                        "auteur", posts.get(0).getAuteurNom()
                ));
            }

            logger.info("Test de base de données réussi: {} posts trouvés", count);

        } catch (Exception e) {
            logger.error("Erreur lors du test de base de données: {}", e.getMessage(), e);
            response.put("status", "ERROR");
            response.put("message", "Erreur de connexion à la base de données: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts() {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Récupération de tous les posts");
            List<Post> posts = postRepository.findAllByOrderByDatePublicationDesc();
            response.put("status", "SUCCESS");
            response.put("count", posts.size());
            response.put("posts", posts.stream().map(post -> Map.of(
                    "id", post.getId(),
                    "titre", post.getTitre(),
                    "auteur", post.getAuteurNom(),
                    "date", post.getDatePublication()
            )).toList());

            logger.info("Posts récupérés avec succès: {}", posts.size());

        } catch (Exception e) {
            logger.error("Erreur lors de la récupération des posts: {}", e.getMessage(), e);
            response.put("status", "ERROR");
            response.put("message", "Erreur lors de la récupération des posts: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }

        return ResponseEntity.ok(response);
    }
}
