package app.project_fin_d_etude.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class KeycloakSecurityConfig {

    // Constantes pour les chemins et URLs
    private static final String[] STATIC_RESOURCES = {
        "/VAADIN/**", "/HEARTBEAT/**", "/UIDL/**", "/PUSH/**",
        "/css/**", "/js/**", "/images/**",
        "/", "/about", "/contact"
    };
    private static final String LOGIN_PAGE = "/oauth2/authorization/keycloak";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String LOGOUT_SUCCESS_URL = "/?logout";
    private static final String COOKIE_JSESSIONID = "JSESSIONID";

    /**
     * Fournit le provider d'authentification Keycloak avec un mapping simple
     * des rôles.
     */
    @Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
        provider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        return provider;
    }

    /**
     * Stratégie de gestion de session pour Keycloak.
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Configuration principale de la sécurité Spring (filtrage, login, logout,
     * etc).
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactive la protection CSRF (Vaadin gère déjà la sécurité côté client)
                .csrf(csrf -> csrf.disable())
                // Gestion de la session
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                )
                // Autorisation des requêtes
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(STATIC_RESOURCES).permitAll()
                .anyRequest().authenticated()
                )
                // Configuration OAuth2 (Keycloak)
                .oauth2Login(oauth2 -> oauth2
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                )
                // Configuration du logout
                .logout(logout -> logout
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies(COOKIE_JSESSIONID)
                );

        return http.build();
    }

    /**
     * Permet à Keycloak d'utiliser la configuration Spring Boot
     * (application.properties/yml).
     */
    @Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
