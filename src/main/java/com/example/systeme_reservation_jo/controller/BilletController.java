package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.service.BilletService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
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

    @PostMapping
    public ResponseEntity<Billet> createBillet(@Valid @RequestBody Billet billet) {
        logger.info("Tentative de création d'un billet avec les données : {}", billet);

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
        logger.info("Mise à jour du billet avec ID : {}", id);
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
