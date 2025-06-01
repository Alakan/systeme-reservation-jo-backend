package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.StatutBillet;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.model.Evenement;

import java.util.List;
import java.util.Optional;

public interface BilletService {
    Billet saveBillet(Billet billet);
    List<Billet> getAllBillets();
    Optional<Billet> getBilletById(Long id);
    Billet updateBillet(Long id, Billet billetDetails);
    void deleteBillet(Long id);
    List<Billet> getBilletsByEvenement(Evenement evenement);
    List<Billet> getBilletsByUtilisateur(Utilisateur utilisateur);
    List<Billet> getBilletsByStatut(StatutBillet statut);
    Optional<Billet> getBilletByReservationId(Long reservationId);
    boolean existsByNumeroBillet(String numeroBillet);
}

