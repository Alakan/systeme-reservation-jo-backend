package com.example.systeme_reservation_jo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @Value("${app.jwtSecret}")
    private String jwtSecret;

    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs;

    /**
     * Retourne une clé de signature sécurisée pour l'algorithme HS512.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length * 8 < 512) { // Vérifie la taille en bits
            logger.warn("La clé définie dans jwtSecret est trop courte pour HS512. Taille actuelle : " + keyBytes.length * 8 + " bits");
            throw new IllegalStateException("Clé JWT non conforme pour HS512");
        }
        return Keys.hmacShaKeyFor(keyBytes); // Crée une clé conforme
    }

    /**
     * Génère un token JWT à partir de l'authentification utilisateur.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * Extrait l'email (username) à partir du token JWT.
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    /**
     * Valide le token JWT pour vérifier sa signature et sa validité.
     */
    public boolean validateToken(String authToken) {
        try {
            logger.info("Validation du token brut : " + authToken);
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Signature JWT invalide : ", ex);
        } catch (MalformedJwtException ex) {
            logger.error("JWT malformé : ", ex);
        } catch (ExpiredJwtException ex) {
            logger.error("JWT expiré : ", ex);
        } catch (UnsupportedJwtException ex) {
            logger.error("JWT non supporté : ", ex);
        } catch (IllegalArgumentException ex) {
            logger.error("Claims JWT vides : ", ex);
        }
        return false;
    }
}
