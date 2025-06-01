package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Récupère le profil de l'utilisateur connecté.
     * Utilise l'email contenu dans le token JWT pour chercher l'utilisateur.
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        // Récupération de l'email de l'utilisateur depuis le contexte de sécurité
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            UtilisateurDTO dto = new UtilisateurDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            // On n'expose pas le mot de passe pour des raisons de sécurité
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }
    }

    /**
     * Permet à l'utilisateur connecté de mettre à jour son profil.
     * L'utilisateur peut modifier son email, son nom d'utilisateur et éventuellement son mot de passe.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(@Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        // Récupération de l'utilisateur connecté depuis le token
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(email);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }
        Utilisateur user = utilisateurOpt.get();

        // Mise à jour des informations autorisées
        user.setEmail(utilisateurDTO.getEmail());
        user.setUsername(utilisateurDTO.getUsername());

        // Si un mot de passe est fourni, on le transmet au service (celui-ci se chargera de l'encoder)
        if (utilisateurDTO.getPassword() != null && !utilisateurDTO.getPassword().trim().isEmpty()) {
            user.setPassword(utilisateurDTO.getPassword());
        }

        // Mise à jour de l'utilisateur grâce au service
        Utilisateur updatedUser = utilisateurService.updateUtilisateur(user.getId(), user);

        // Conversion en DTO pour la réponse
        UtilisateurDTO updatedDTO = new UtilisateurDTO();
        updatedDTO.setId(updatedUser.getId());
        updatedDTO.setEmail(updatedUser.getEmail());
        updatedDTO.setUsername(updatedUser.getUsername());
        return ResponseEntity.ok(updatedDTO);
    }

    /**
     * Récupère un utilisateur via son identifiant.
     * Cet endpoint est utile, par exemple, pour pré-remplir les formulaires d'administration.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateurOpt = utilisateurService.getUtilisateurById(id);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            UtilisateurDTO dto = new UtilisateurDTO();
            dto.setId(user.getId());
            dto.setEmail(user.getEmail());
            dto.setUsername(user.getUsername());
            // On évite d'exposer le password
            return ResponseEntity.ok(dto);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé.");
        }
    }
}
