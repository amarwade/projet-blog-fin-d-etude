package app.project_fin_d_etude.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.util.stream.Stream;

/**
 * Utilitaires pour la sécurité et l'authentification utilisateur.
 */
public class SecurityUtils {

    /**
     * Retourne l'email (ou l'identifiant principal) de l'utilisateur
     * actuellement connecté.
     *
     * @return L'email de l'utilisateur connecté, ou null si non authentifié
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() != null) {
            return auth.getName(); // retourne l'email ou l'identifiant principal
        }
        return null;
    }

    /**
     * Vérifie si un utilisateur est connecté.
     *
     * @return true si l'utilisateur est authentifié, false sinon
     */
    public static boolean isUserAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * Vérifie si la requête est une requête interne du framework Vaadin.
     *
     * @param request La requête HTTP
     * @return true si c'est une requête interne Vaadin, false sinon
     */
    public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(HandlerHelper.RequestType.values())
                        .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Vérifie si l'utilisateur courant possède un rôle donné.
     *
     * @param role Le nom du rôle à vérifier (ex : "ADMIN")
     * @return true si l'utilisateur possède ce rôle, false sinon
     */
    public static boolean hasRole(String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getAuthorities() != null) {
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
        }
        return false;
    }

    /**
     * Retourne le nom complet de l'utilisateur connecté (si disponible via
     * OidcUser), sinon null.
     */
    public static String getCurrentUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) auth.getPrincipal();
            String fullName = oidcUser.getFullName();
            if (fullName != null && !fullName.isBlank()) {
                return fullName;
            }
            // Fallback prénom + nom
            String givenName = oidcUser.getGivenName();
            String familyName = oidcUser.getFamilyName();
            if (givenName != null || familyName != null) {
                return ((givenName != null ? givenName : "") + " " + (familyName != null ? familyName : "")).trim();
            }
        }
        return null;
    }
}
