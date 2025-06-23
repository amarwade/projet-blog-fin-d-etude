package app.project_fin_d_etude.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration de la sécurité générale de l'application (hors Keycloak).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Fournit un encodeur de mots de passe BCrypt pour le hachage sécurisé des
     * mots de passe utilisateurs.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
