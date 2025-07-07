package app.project_fin_d_etude.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe contenant toutes les constantes de routes de l'application. Centralise
 * la gestion des URLs pour éviter les incohérences.
 */
public final class Routes {

    private static final Logger logger = LoggerFactory.getLogger(Routes.class);

    // Routes publiques
    public static final String HOME = "/";
    public static final String ARTICLES = "/articles";
    public static final String ABOUT = "/about";
    public static final String CONTACT = "/contact";
    public static final String LOGIN = "/login";

    // Routes utilisateur
    public static final String USER_PROFILE = "/user/profile";
    public static final String USER_INFO = "/user-info";
    public static final String USER_CREATE_POST = "/user/create-post";
    public static final String USER_ARTICLE = "/user/article";

    // Routes d'authentification
    public static final String LOGOUT = "/logout";

    // Routes d'administration
    public static final String ADMIN = "/admin";
    public static final String ADMIN_POSTS = "/admin/posts";
    public static final String ADMIN_COMMENTAIRES = "/admin/commentaires";
    public static final String ADMIN_MESSAGES = "/admin/messages";
    public static final String ADMIN_USERS = "/admin/users";

    // Routes API (si nécessaire)
    public static final String API_BASE = "/api";
    public static final String API_POSTS = API_BASE + "/posts";
    public static final String API_COMMENTAIRES = API_BASE + "/commentaires";
    public static final String API_MESSAGES = API_BASE + "/messages";

    /**
     * Génère l'URL d'un article utilisateur à partir de son identifiant.
     *
     * @param postId L'identifiant du post
     * @return L'URL de l'article utilisateur
     */
    public static String getUserArticleUrl(Long postId) {
        if (postId == null) {
            logger.warn("PostId null fourni à getUserArticleUrl");
            return USER_ARTICLE;
        }
        return USER_ARTICLE + "/" + postId;
    }

    /**
     * Génère l'URL d'un article public à partir de son identifiant.
     *
     * @param postId L'identifiant du post
     * @return L'URL de l'article public
     */
    public static String getPublicArticleUrl(Long postId) {
        if (postId == null) {
            logger.warn("PostId null fourni à getPublicArticleUrl");
            return ARTICLES;
        }
        return ARTICLES + "/" + postId;
    }

    /**
     * Génère l'URL d'édition d'un article à partir de son identifiant.
     *
     * @param postId L'identifiant du post
     * @return L'URL d'édition de l'article
     */
    public static String getEditArticleUrl(Long postId) {
        if (postId == null) {
            logger.warn("PostId null fourni à getEditArticleUrl");
            return USER_CREATE_POST;
        }
        return USER_CREATE_POST + "/" + postId;
    }

    /**
     * Génère l'URL d'administration d'un article à partir de son identifiant.
     *
     * @param postId L'identifiant du post
     * @return L'URL d'administration de l'article
     */
    public static String getAdminArticleUrl(Long postId) {
        if (postId == null) {
            logger.warn("PostId null fourni à getAdminArticleUrl");
            return ADMIN_POSTS;
        }
        return ADMIN_POSTS + "/" + postId;
    }

    /**
     * Génère l'URL d'administration d'un commentaire à partir de son
     * identifiant.
     *
     * @param commentaireId L'identifiant du commentaire
     * @return L'URL d'administration du commentaire
     */
    public static String getAdminCommentaireUrl(Long commentaireId) {
        if (commentaireId == null) {
            logger.warn("CommentaireId null fourni à getAdminCommentaireUrl");
            return ADMIN_COMMENTAIRES;
        }
        return ADMIN_COMMENTAIRES + "/" + commentaireId;
    }

    /**
     * Génère l'URL d'administration d'un message à partir de son identifiant.
     *
     * @param messageId L'identifiant du message
     * @return L'URL d'administration du message
     */
    public static String getAdminMessageUrl(Long messageId) {
        if (messageId == null) {
            logger.warn("MessageId null fourni à getAdminMessageUrl");
            return ADMIN_MESSAGES;
        }
        return ADMIN_MESSAGES + "/" + messageId;
    }

    /**
     * Génère l'URL d'administration d'un utilisateur à partir de son
     * identifiant.
     *
     * @param userId L'identifiant de l'utilisateur
     * @return L'URL d'administration de l'utilisateur
     */
    public static String getAdminUserUrl(String userId) {
        if (userId == null || userId.trim().isEmpty()) {
            logger.warn("UserId null ou vide fourni à getAdminUserUrl");
            return ADMIN_USERS;
        }
        return ADMIN_USERS + "/" + userId;
    }

    /**
     * Vérifie si une route est une route publique.
     *
     * @param route La route à vérifier
     * @return true si c'est une route publique, false sinon
     */
    public static boolean isPublicRoute(String route) {
        if (route == null) {
            return false;
        }
        return HOME.equals(route)
                || ARTICLES.equals(route)
                || ABOUT.equals(route)
                || CONTACT.equals(route)
                || LOGIN.equals(route)
                || route.startsWith(ARTICLES + "/");
    }

    /**
     * Vérifie si une route est une route d'administration.
     *
     * @param route La route à vérifier
     * @return true si c'est une route d'administration, false sinon
     */
    public static boolean isAdminRoute(String route) {
        if (route == null) {
            return false;
        }
        return route.startsWith(ADMIN);
    }

    /**
     * Vérifie si une route est une route utilisateur.
     *
     * @param route La route à vérifier
     * @return true si c'est une route utilisateur, false sinon
     */
    public static boolean isUserRoute(String route) {
        if (route == null) {
            return false;
        }
        return route.startsWith("/user")
                || USER_INFO.equals(route)
                || route.startsWith(USER_ARTICLE + "/");
    }

    /**
     * Vérifie si une route est une route d'authentification.
     *
     * @param route La route à vérifier
     * @return true si c'est une route d'authentification, false sinon
     */
    public static boolean isAuthRoute(String route) {
        if (route == null) {
            return false;
        }
        return LOGIN.equals(route) || LOGOUT.equals(route);
    }

    /**
     * Nettoie et normalise une route.
     *
     * @param route La route à nettoyer
     * @return La route nettoyée
     */
    public static String cleanRoute(String route) {
        if (route == null) {
            return HOME;
        }

        String cleaned = route.trim();

        // Assure qu'une route commence par /
        if (!cleaned.startsWith("/")) {
            cleaned = "/" + cleaned;
        }

        // Supprime les slashes multiples
        cleaned = cleaned.replaceAll("/+", "/");

        // Supprime le slash final sauf pour la route racine
        if (cleaned.length() > 1 && cleaned.endsWith("/")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }

        return cleaned;
    }

    private Routes() {
        // Classe utilitaire, constructeur privé
    }
}
