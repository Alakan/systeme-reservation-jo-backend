package com.example.systeme_reservation_jo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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
     * Le token inclut désormais un claim "roles" qui contient une liste de rôles.
     */
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Récupère les rôles à partir des autorités de l'utilisateur
        List<String> roles = userDetails.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.toList());

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)  // Ajoute les rôles dans le token
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
