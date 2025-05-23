package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable Long id) {
        Optional<Utilisateur> utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> saveUtilisateur(@Valid @RequestBody Utilisateur utilisateur) {
        if (utilisateurService.existsByEmail(utilisateur.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Collections.singletonMap("message", "L'email est déjà utilisé !"));
        }
        Utilisateur nouvelUtilisateur = utilisateurService.saveUtilisateur(utilisateur);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelUtilisateur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Long id, @Valid @RequestBody Utilisateur utilisateurDetails) {
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurById(id);
        if (utilisateurOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Utilisateur non trouvé"));
        }
        Utilisateur utilisateurModifie = utilisateurService.updateUtilisateur(id, utilisateurDetails);
        return ResponseEntity.ok(utilisateurModifie);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUtilisateur(@PathVariable Long id) {
        Optional<Utilisateur> utilisateurOptional = utilisateurService.getUtilisateurById(id);
        if (utilisateurOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", "Utilisateur non trouvé"));
        }
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Correction du endpoint pour restreindre l'accès aux administrateurs
    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRATEUR')") // ✅ Protection correcte
    public ResponseEntity<List<Utilisateur>> getAdminUsers() {
        List<Utilisateur> admins = utilisateurService.getAllAdminUsers();
        return ResponseEntity.ok(admins);
    }

}
