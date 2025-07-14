package app.project_fin_d_etude.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.project_fin_d_etude.model.Post;
import app.project_fin_d_etude.repository.PostRepository;

/**
 * Contrôleur de fallback pour gérer les routes non trouvées et les erreurs d'accès.
 */
@RestController
@RequestMapping("/api/diagnostic")
public class FallbackController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/posts-count")
    public String postsCount() {
        long count = postRepository.count();
        return "Nombre de posts: " + count;
    }
}
