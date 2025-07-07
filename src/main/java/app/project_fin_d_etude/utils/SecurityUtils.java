package app.project_fin_d_etude.utils;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import com.vaadin.flow.server.HandlerHelper;
import com.vaadin.flow.shared.ApplicationConstants;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utilitaires pour la sécurité et l'authentification utilisateur.
 */
public final class SecurityUtils {

    private static final Logger logger = LoggerFactory.getLogger(SecurityUtils.class);
    private static final String ANONYMOUS_USER = "anonymousUser";
    private static final String ROLE_PREFIX = "ROLE_";

    private SecurityUtils() {
        // Classe utilitaire, constructeur privé
    }

    /**
     * Retourne l'email (ou l'identifiant principal) de l'utilisateur
     * actuellement connecté (compatible OIDC).
     *
     * @return L'email OIDC de l'utilisateur connecté, ou null si non
     * authentifié
     */
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return getCurrentUserEmail(auth);
    }

    /**
     * Retourne l'email (ou l'identifiant principal) de l'utilisateur à partir
     * d'un Authentication explicite (compatible OIDC).
     *
     * @param auth Authentication à utiliser
     * @return L'email OIDC de l'utilisateur connecté, ou null si non
     * authentifié
     */
    public static String getCurrentUserEmail(Authentication auth) {
        if (isValidAuthentication(auth)) {
            Object principal = auth.getPrincipal();
            if (principal instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser oidcUser) {
                String email = oidcUser.getEmail();
                if (email != null && !email.isBlank()) {
                    return email;
                }
            }
            return auth.getName();
        }
        return null;
    }

    /**
     * Vérifie si un utilisateur est connecté (authentifié OIDC).
     *
     * @return true si l'utilisateur est authentifié OIDC, false sinon
     */
    public static boolean isUserAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return isValidAuthentication(auth);
    }

    /**
     * Vérifie si la requête est une requête interne du framework Vaadin.
     *
     * @param request La requête HTTP
     * @return true si c'est une requête interne Vaadin, false sinon
     */
    public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        if (request == null) {
            logger.warn("Requête HTTP null fournie à isFrameworkInternalRequest");
            return false;
        }

        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
                && Stream.of(HandlerHelper.RequestType.values())
                        .anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Vérifie si l'utilisateur courant possède un rôle donné (utilise
     * AppRoles).
     *
     * @param role Le nom du rôle à vérifier (ex : AppRoles.ADMIN)
     * @return true si l'utilisateur possède ce rôle, false sinon
     */
    public static boolean hasRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            logger.warn("Rôle null ou vide fourni à hasRole");
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getAuthorities() != null) {
            String normalizedRole = role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
            return auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals(normalizedRole));
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur courant possède au moins un des rôles donnés.
     *
     * @param roles Les noms des rôles à vérifier
     * @return true si l'utilisateur possède au moins un des rôles, false sinon
     */
    public static boolean hasAnyRole(String... roles) {
        if (roles == null || roles.length == 0) {
            logger.warn("Aucun rôle fourni à hasAnyRole");
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getAuthorities() != null) {
            return Stream.of(roles)
                    .filter(role -> role != null && !role.trim().isEmpty())
                    .anyMatch(role -> {
                        String normalizedRole = role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
                        return auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals(normalizedRole));
                    });
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur courant possède tous les rôles donnés.
     *
     * @param roles Les noms des rôles à vérifier
     * @return true si l'utilisateur possède tous les rôles, false sinon
     */
    public static boolean hasAllRoles(String... roles) {
        if (roles == null || roles.length == 0) {
            logger.warn("Aucun rôle fourni à hasAllRoles");
            return false;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getAuthorities() != null) {
            return Stream.of(roles)
                    .filter(role -> role != null && !role.trim().isEmpty())
                    .allMatch(role -> {
                        String normalizedRole = role.startsWith(ROLE_PREFIX) ? role : ROLE_PREFIX + role;
                        return auth.getAuthorities().stream()
                                .anyMatch(a -> a.getAuthority().equals(normalizedRole));
                    });
        }
        return false;
    }

    /**
     * Retourne le nom complet de l'utilisateur connecté (si disponible via
     * OidcUser), sinon null.
     *
     * @return Le nom complet de l'utilisateur ou null
     */
    public static String getCurrentUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getPrincipal() instanceof OidcUser) {
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

    /**
     * Retourne le prénom de l'utilisateur connecté.
     *
     * @return Le prénom de l'utilisateur ou null
     */
    public static String getCurrentUserGivenName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) auth.getPrincipal();
            return oidcUser.getGivenName();
        }
        return null;
    }

    /**
     * Retourne le nom de famille de l'utilisateur connecté.
     *
     * @return Le nom de famille de l'utilisateur ou null
     */
    public static String getCurrentUserFamilyName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) auth.getPrincipal();
            return oidcUser.getFamilyName();
        }
        return null;
    }

    /**
     * Retourne l'URL de l'image de profil de l'utilisateur connecté.
     *
     * @return L'URL de l'image de profil ou null
     */
    public static String getCurrentUserPictureUrl() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isValidAuthentication(auth) && auth.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) auth.getPrincipal();
            return oidcUser.getPicture();
        }
        return null;
    }

    /**
     * Vérifie si l'authentification est valide (authentifié et principal OIDC).
     *
     * @param auth L'authentification à vérifier
     * @return true si l'authentification est valide et OIDC, false sinon
     */
    private static boolean isValidAuthentication(Authentication auth) {
        return auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof org.springframework.security.oauth2.core.oidc.user.OidcUser;
    }

    /**
     * Retourne l'objet Authentication actuel.
     *
     * @return L'authentification actuelle ou null
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Vérifie si l'utilisateur actuel est un administrateur (centralisé).
     */
    public static boolean isAdmin() {
        return hasRole(app.project_fin_d_etude.config.AppRoles.ADMIN);
    }

    /**
     * Vérifie si l'utilisateur actuel est un utilisateur standard (centralisé).
     */
    public static boolean isUser() {
        return hasRole(app.project_fin_d_etude.config.AppRoles.USER);
    }
}
