package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvenementService {
    // Pour la gestion administrative (tous les événements)
    List<Evenement> getAllEvenements();

    // Pour la vue publique (seulement les événements actifs)
    List<Evenement> getAllEvenementsPublic();

    Optional<Evenement> getEvenementById(Long id);
    Evenement createEvenement(Evenement evenement);
    Evenement updateEvenement(Long id, Evenement evenementDetails);
    List<Evenement> searchEvenements(String motCle);
    List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end);

    // Désactivation d'un événement (mise à jour du champ actif à false au lieu d'une suppression définitive)
    Evenement desactiverEvenement(Long id);
    // Réactivation d'un événement : mise à jour du champ actif à true
    Evenement reactiverEvenement(Long id);

}
