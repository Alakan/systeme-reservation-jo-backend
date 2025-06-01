package com.example.systeme_reservation_jo.security;

import com.example.systeme_reservation_jo.service.UtilisateurDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("!test")
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UtilisateurDetailsServiceImpl utilisateurDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.info("Traitement de la requête pour l'URI: " + requestURI);

        // Bypass le traitement du token pour les endpoints d'authentification
        if (requestURI.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Récupère le token JWT de la requête
            String jwt = getJwtFromRequest(request);
            if (jwt == null) {
                logger.warn("Aucun token JWT trouvé dans la requête.");
            } else {
                logger.info("Token brut reçu : " + jwt);
            }

            // Vérifie si le token est valide
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                // Récupère l'email à partir du JWT
                String email = tokenProvider.getUsernameFromJWT(jwt);
                logger.info("Email extrait du token : " + email);

                // Charge les détails de l'utilisateur
                UserDetails userDetails = utilisateurDetailsService.loadUserByUsername(email);
                logger.info("Authorities récupérées pour l'utilisateur : " + userDetails.getAuthorities());

                // Crée l'objet d'authentification
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définit l'utilisateur authentifié dans le SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.info("Authentification définie dans SecurityContext pour : " + email);
            } else {
                logger.warn("Le token est invalide ou vide.");
            }
        } catch (Exception ex) {
            logger.error("Erreur lors du traitement du JWT : ", ex);
        }

        // Poursuit la chaîne de filtres
        filterChain.doFilter(request, response);
    }

    // Méthode pour extraire le token JWT de la requête
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7); // Supprime "Bearer "
            logger.info("Token extrait après 'Bearer ': " + token);
            return token;
        }
        return null;
    }
}
