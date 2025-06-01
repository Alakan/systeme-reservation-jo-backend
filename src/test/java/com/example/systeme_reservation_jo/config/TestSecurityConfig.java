package com.example.systeme_reservation_jo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("test")
public class TestSecurityConfig {

    @Bean("testFilterChain")
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Désactivation de CSRF pour simplifier les tests
                .csrf(AbstractHttpConfigurer::disable)
                // Ici, nous ne recréons pas le filtre JWT ni le CORS
                .authorizeHttpRequests(authz -> authz
                        // Autoriser l'accès libre aux endpoints d'authentification
                        .requestMatchers("/api/auth/**").permitAll()
                        // Par exemple, garder le contrôle sur certains endpoints
                        .requestMatchers("/api/utilisateurs/admin").hasAuthority("ROLE_ADMINISTRATEUR")
                        .requestMatchers("/api/evenements/**").hasRole("ADMINISTRATEUR")
                        .requestMatchers("/api/reservations/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        .requestMatchers("/api/billets/**").hasAnyRole("ADMINISTRATEUR", "UTILISATEUR")
                        // Pour tous les autres endpoints, exiger une authentification
                        .anyRequest().authenticated()
                );
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
