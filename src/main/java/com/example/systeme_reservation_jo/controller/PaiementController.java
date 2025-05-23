package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.service.PaiementService;
import com.example.systeme_reservation_jo.service.ReservationService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    @Autowired
    private PaiementService paiementService;

    @Autowired
    private ReservationService reservationService;

    @GetMapping
    public List<Paiement> getAllPaiements() {
        return paiementService.getAllPaiements();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paiement> getPaiementById(@PathVariable Long id) {
        return paiementService.getPaiementById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Paiement> createPaiement(@Valid @RequestBody Paiement paiement) {
        // ✅ Vérification que le paiement est bien lié à une réservation
        if (paiement.getReservation() == null || paiement.getReservation().getId() == null) {
            return ResponseEntity.badRequest().body(null); // Erreur 400 si la réservation est manquante
        }

        // ✅ Récupération de la réservation associée
        Reservation reservation = reservationService.getReservationById(paiement.getReservation().getId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée avec l'id : " + paiement.getReservation().getId()));

        paiement.setReservation(reservation); // ✅ Association correcte de la réservation

        Paiement savedPaiement = paiementService.savePaiement(paiement);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPaiement);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paiement> updatePaiement(@PathVariable Long id, @Valid @RequestBody Paiement paiementDetails) {
        Paiement updatedPaiement = paiementService.updatePaiement(id, paiementDetails);
        return ResponseEntity.ok(updatedPaiement);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaiement(@PathVariable Long id) {
        try {
            paiementService.deletePaiement(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
