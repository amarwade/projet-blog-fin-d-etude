package app.project_fin_d_etude.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

//import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
//import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
//import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true, securedEnabled = true)
public class KeycloakSecurityConfig extends VaadinWebSecurity {

    // Constantes pour les chemins et URLs
    private static final String[] STATIC_RESOURCES = {
        "/VAADIN/", "/HEARTBEAT/", "/UIDL/", "/PUSH/",
        "/css/", "/js/", "/images/", "/themes/"
    };
    private static final String[] PUBLIC_ROUTES = {
        "/", "/articles", "/about", "/contact", "/login"
    };
    private static final String LOGIN_PAGE = "/login";
    private static final String DEFAULT_SUCCESS_URL = "/";
    private static final String LOGOUT_SUCCESS_URL = "/login?logout";
    private static final String COOKIE_JSESSIONID = "JSESSIONID";

    /**
     * Fournit le provider d'authentification Keycloak avec un mapping simple
     * des rôles.
     */
    /*@Bean
    public KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        KeycloakAuthenticationProvider provider = new KeycloakAuthenticationProvider();
        provider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        return provider;
    }*/
    /**
     * Stratégie de gestion de session pour Keycloak.
     */
    @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    /**
     * Permet à Keycloak d'utiliser la configuration Spring Boot
     * (application.properties/yml).
     */
    /*@Bean
    public KeycloakSpringBootConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }*/
    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> customOidcUserService() {
        return userRequest -> {
            OidcUserService delegate = new OidcUserService();
            OidcUser oidcUser = delegate.loadUser(userRequest);

            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            // Extraire les rôles du claim "roles"
            Map<String, Object> claims = oidcUser.getClaims();
            Object rolesClaim = claims.get("roles");
            if (rolesClaim instanceof Collection<?>) {
                for (Object role : (Collection<?>) rolesClaim) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            // Ajouter les autres authorities par défaut
            mappedAuthorities.addAll(oidcUser.getAuthorities());

            return new org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser(
                    mappedAuthorities,
                    oidcUser.getIdToken(),
                    oidcUser.getUserInfo()
            );
        };
    }

    /**
     * Configuration principale de la sécurité Spring (filtrage, login, logout,
     * etc).
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http); // Vaadin config

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .sessionAuthenticationStrategy(sessionAuthenticationStrategy())
                )
                .oauth2Login(oauth2 -> oauth2
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
                .userInfoEndpoint(userInfo -> userInfo
                .oidcUserService(customOidcUserService())
                )
                )
                .logout(logout -> logout
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies(COOKIE_JSESSIONID)
                )
                .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> response.sendRedirect("/login"))
                .accessDeniedPage("/login")
                );
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
