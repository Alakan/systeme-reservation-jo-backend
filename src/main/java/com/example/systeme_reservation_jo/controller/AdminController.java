package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.example.systeme_reservation_jo.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMINISTRATEUR')")
public class AdminController {

    private final UtilisateurService utilisateurService;
    private final EvenementService evenementService;
    private final ReservationService reservationService;

    public AdminController(UtilisateurService utilisateurService,
                           EvenementService evenementService,
                           ReservationService reservationService) {
        this.utilisateurService = utilisateurService;
        this.evenementService = evenementService;
        this.reservationService = reservationService;
    }

    // Récupérer tous les utilisateurs (y compris les administrateurs)
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurService.getAllUtilisateurs();
        return ResponseEntity.ok(utilisateurs);
    }

    // Modification d'un utilisateur par l'admin
    @PutMapping("/utilisateurs/{id}")
    public ResponseEntity<?> updateUtilisateur(@PathVariable Long id,
                                               @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            // Conversion du DTO en entité
            Utilisateur utilisateurToUpdate = dtoToEntity(utilisateurDTO);
            Utilisateur updatedUtilisateur = utilisateurService.updateUtilisateur(id, utilisateurToUpdate);
            return ResponseEntity.ok(updatedUtilisateur);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    // Suppression d'un utilisateur (suppression physique)
    @DeleteMapping("/utilisateurs/{id}")
    public ResponseEntity<String> deleteUtilisateur(@PathVariable Long id) {
        utilisateurService.deleteUtilisateur(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    // Récupérer tous les événements
    @GetMapping("/evenements")
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        List<Evenement> evenements = evenementService.getAllEvenements();
        return ResponseEntity.ok(evenements);
    }

    // Désactivation d'un événement (mise à jour du champ actif à false)
    @PutMapping("/evenements/{id}/desactiver")
    public ResponseEntity<?> desactiverEvenement(@PathVariable Long id) {
        try {
            Evenement evenementDesactive = evenementService.desactiverEvenement(id);
            return ResponseEntity.ok(evenementDesactive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la désactivation de l'événement : " + e.getMessage());
        }
    }

    // Réactivation (activation) d'un événement (champ actif à true)
    @PutMapping("/evenements/{id}/reactiver")
    public ResponseEntity<?> reactiverEvenement(@PathVariable Long id) {
        try {
            Evenement evenementActive = evenementService.reactiverEvenement(id);
            return ResponseEntity.ok(evenementActive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la réactivation de l'événement : " + e.getMessage());
        }
    }

    // Récupérer toutes les réservations
    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    // Désactivation d'une réservation (mise à jour du champ actif à false)
    @PutMapping("/reservations/{id}/desactiver")
    public ResponseEntity<?> desactiverReservation(@PathVariable Long id) {
        try {
            Reservation reservationDesactive = reservationService.desactiverReservation(id);
            return ResponseEntity.ok(reservationDesactive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la désactivation de la réservation : " + e.getMessage());
        }
    }

    // Réactivation (activation) d'une réservation (champ actif à true)
    @PutMapping("/reservations/{id}/reactiver")
    public ResponseEntity<?> reactiverReservation(@PathVariable Long id) {
        try {
            Reservation reservationActive = reservationService.reactiverReservation(id);
            return ResponseEntity.ok(reservationActive);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Erreur lors de la réactivation de la réservation : " + e.getMessage());
        }
    }

    /**
     * Méthode utilitaire pour convertir un UtilisateurDTO en Utilisateur.
     */
    private Utilisateur dtoToEntity(UtilisateurDTO dto) {
        Utilisateur user = new Utilisateur();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        // Le mot de passe est transmis si présent (le service se chargera de l'encoder)
        user.setPassword(dto.getPassword());
        return user;
    }
}
