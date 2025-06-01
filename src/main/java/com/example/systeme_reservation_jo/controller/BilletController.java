package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.service.BilletService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les billets.
 */
@RestController
@RequestMapping("/api/billets")
public class BilletController {

    private static final Logger logger = LoggerFactory.getLogger(BilletController.class);
    private final BilletService billetService;

    public BilletController(BilletService billetService) {
        this.billetService = billetService;
    }

    @GetMapping
    public List<Billet> getAllBillets() {
        logger.info("Récupération de tous les billets");
        return billetService.getAllBillets();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Billet> getBilletById(@PathVariable Long id) {
        logger.info("Récupération du billet avec ID : {}", id);
        return billetService.getBilletById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Nouvel endpoint pour récupérer un billet par l'ID de la réservation
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<Billet> getBilletByReservationId(@PathVariable Long reservationId) {
        logger.info("Récupération du billet pour la réservation ID : {}", reservationId);
        return billetService.getBilletByReservationId(reservationId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
    }

    @PostMapping
    public ResponseEntity<Billet> createBillet(@Valid @RequestBody Billet billet) {
        // Loguer le payload reçu pour vérifier que l'objet contient bien les données attendues.
        logger.info("Payload reçu pour création de billet : {}", billet);

        // Vérification de duplication du numéro de billet
        if (billetService.existsByNumeroBillet(billet.getNumeroBillet())) {
            logger.warn("Échec de création - Le numéro de billet {} existe déjà", billet.getNumeroBillet());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Billet savedBillet = billetService.saveBillet(billet);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBillet);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Billet> updateBillet(@PathVariable Long id, @Valid @RequestBody Billet billetDetails) {
        logger.info("Mise à jour du billet avec ID : {}. Payload : {}", id, billetDetails);
        Billet updatedBillet = billetService.updateBillet(id, billetDetails);
        return ResponseEntity.ok(updatedBillet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBillet(@PathVariable Long id) {
        logger.info("Suppression du billet avec ID : {}", id);
        billetService.deleteBillet(id);
        return ResponseEntity.noContent().build();
    }
}
