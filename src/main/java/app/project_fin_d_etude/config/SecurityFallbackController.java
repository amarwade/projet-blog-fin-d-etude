package app.project_fin_d_etude.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrôleur de fallback pour gérer les erreurs d'accès et rediriger les utilisateurs non connectés.
 */
@Controller
public class SecurityFallbackController {

    /**
     * Redirige les utilisateurs non connectés vers la page de connexion.
     * Cette méthode est appelée quand Spring Security intercepte une tentative
     * d'accès à une ressource protégée.
     */
    @RequestMapping("/access-denied")
    public String accessDenied() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Si l'utilisateur n'est pas authentifié, rediriger vers la page de connexion
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return "redirect:/login";
        }
        
        // Si l'utilisateur est authentifié mais n'a pas les bonnes permissions,
        // rediriger vers la page d'accueil
        return "redirect:/";
    }
} 