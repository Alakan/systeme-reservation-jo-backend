package com.example.systeme_reservation_jo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Data Transfer Object pour l'utilisateur.
 * Ce DTO est utilisé pour afficher ou modifier le profil de l'utilisateur côté frontend.
 * Le champ "password" n'est utilisé que lors des mises à jour (écriture uniquement).
 */
@Data
public class UtilisateurDTO {
    private Long id;
    private String email;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // Optionnel, utilisé uniquement lors des mises à jour
}
