package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EvenementService {
    List<Evenement> getAllEvenements();
    Optional<Evenement> getEvenementById(Long id);
    Evenement createEvenement(Evenement evenement);
    Evenement updateEvenement(Long id, Evenement evenementDetails);
    void deleteEvenement(Long id);
    List<Evenement> searchEvenements(String motCle);

    List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end); // 🔹 Ajout de cette méthode
}
