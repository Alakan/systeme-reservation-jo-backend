package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evenements")
@CrossOrigin(origins = "http://localhost:3000") // À adapter selon votre configuration front-end
public class EvenementController {

    private static final Logger logger = LoggerFactory.getLogger(EvenementController.class);

    private final EvenementService evenementService;

    @Autowired
    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    /**
     * Récupération de tous les événements publics (seulement ceux actifs).
     */
    @GetMapping
    public ResponseEntity<List<Evenement>> getAllEvenements() {
        // Retourne uniquement les événements actifs pour la vue publique
        return ResponseEntity.ok(evenementService.getAllEvenementsPublic());
    }

    /**
     * Récupération d'un événement par son id (seulement s'il est actif).
     */
    @GetMapping("/{id}")
    public ResponseEntity<Evenement> getEvenementById(@PathVariable Long id) {
        Optional<Evenement> evenementOpt = evenementService.getEvenementById(id);
        if (evenementOpt.isPresent()) {
            Evenement evenement = evenementOpt.get();
            if (!evenement.isActif()) {
                // Si l'événement est désactivé, on renvoie 404
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(evenement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Création d'un événement.
     * (Cette opération peut être réservée aux administrateurs.)
     */
    @PostMapping
    public ResponseEntity<Evenement> createEvenement(@Valid @RequestBody Evenement evenement) {
        logger.info("Création de l'événement avec les données : {}", evenement);
        Evenement createdEvenement = evenementService.createEvenement(evenement);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvenement);
    }

    /**
     * Mise à jour d'un événement existant.
     * (Opération réservée aux administrateurs.)
     */
    @PutMapping("/{id}")
    public ResponseEntity<Evenement> updateEvenement(@PathVariable Long id, @Valid @RequestBody Evenement evenement) {
        Optional<Evenement> existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Evenement updatedEvenement = evenementService.updateEvenement(id, evenement);
        return ResponseEntity.ok(updatedEvenement);
    }

    /**
     * Désactivation d'un événement (opération d'administration),
     * qui met à jour le champ actif à false.
     */
    @PutMapping("/{id}/desactiver")
    public ResponseEntity<Evenement> desactiverEvenement(@PathVariable Long id) {
        Optional<Evenement> existingEvenement = evenementService.getEvenementById(id);
        if (existingEvenement.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Evenement evenementDesactive = evenementService.desactiverEvenement(id);
        return ResponseEntity.ok(evenementDesactive);
    }

    /**
     * Récupération d'événements entre deux dates (filtrés pour ne retourner que les actifs).
     */
    @GetMapping("/between-dates")
    public ResponseEntity<List<Evenement>> getEvenementsBetweenDates(
            @RequestParam("start") LocalDateTime dateDebut,
            @RequestParam("end") LocalDateTime dateFin) {
        List<Evenement> evenements = evenementService.findEvenementsBetweenDates(dateDebut, dateFin);
        // Filtrage complémentaire au cas où le service ne le ferait pas déjà
        evenements.removeIf(e -> !e.isActif());
        return ResponseEntity.ok(evenements);
    }
}
