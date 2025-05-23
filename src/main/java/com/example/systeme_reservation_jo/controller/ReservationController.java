package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private UtilisateurService utilisateurService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> getReservationById(@PathVariable Long id) {
        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        return reservationOpt.map(reservation -> ResponseEntity.ok((Object) reservation)) // ✅ Assure le bon typage
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body((Object) "Réservation introuvable."));
    }

    @GetMapping("/utilisateur")
    @PreAuthorize("hasRole('UTILISATEUR') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Object> getReservationsByUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        System.out.println("🔍 Email de l'utilisateur authentifié : " + userEmail);

        Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(userEmail);
        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur introuvable.");
        }

        List<Reservation> reservations = reservationService.getReservationsByUtilisateur(utilisateurOpt.get().getId());

        for (Reservation reservation : reservations) {
            if (reservation.getEvenement() == null) {
                System.out.println("⚠ Réservation " + reservation.getId() + " sans événement !");
            } else {
                System.out.println("🔍 Événement récupéré : " + reservation.getEvenement().getTitre());
            }
        }

        return reservations.isEmpty()
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("Vous n'avez aucune réservation.")
                : ResponseEntity.ok(reservations);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            if (reservation.getEvenement() == null || reservation.getUtilisateur() == null) {
                return ResponseEntity.badRequest().body("L'événement et l'utilisateur sont obligatoires.");
            }
            if (reservation.getDateReservation() == null) {
                reservation.setDateReservation(LocalDateTime.now());
            }
            if (reservation.getNombreBillets() <= 0) {
                return ResponseEntity.badRequest().body("Le nombre de billets doit être supérieur à zéro.");
            }

            Optional<Utilisateur> utilisateurOpt = utilisateurService.findByEmail(reservation.getUtilisateur().getEmail());
            if (utilisateurOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Utilisateur non trouvé.");
            }

            reservation.setUtilisateur(utilisateurOpt.get());
            reservation.setStatut(StatutReservation.EN_ATTENTE);

            Reservation savedReservation = reservationService.createReservation(reservation);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedReservation);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création de la réservation : " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> updateReservation(@PathVariable Long id, @Valid @RequestBody Reservation reservationDetails) {
        try {
            Reservation updatedReservation = reservationService.updateReservation(id, reservationDetails);
            return ResponseEntity.ok(updatedReservation);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur lors de la mise à jour de la réservation : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> deleteReservation(@PathVariable Long id) {
        try {
            reservationService.deleteReservation(id);
            return ResponseEntity.ok("Réservation supprimée avec succès.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    @PutMapping("/{id}/paiement")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Object> effectuerPaiement(@PathVariable Long id, @RequestBody ModePaiement modePaiement) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Utilisateur authentifié : " + authentication.getName());
        System.out.println("Rôles de l'utilisateur : " + authentication.getAuthorities());

        Optional<Reservation> reservationOpt = reservationService.getReservationById(id);
        if (reservationOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Réservation introuvable.");
        }

        Reservation reservation = reservationOpt.get();
        System.out.println("Statut actuel de la réservation ID " + id + " : " + reservation.getStatut());

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("La réservation a déjà été payée ou annulée.");
        }

        reservation.setModePaiement(modePaiement);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        reservationService.updateReservation(id, reservation);

        return ResponseEntity.ok("Paiement effectué avec succès !");
    }
}
