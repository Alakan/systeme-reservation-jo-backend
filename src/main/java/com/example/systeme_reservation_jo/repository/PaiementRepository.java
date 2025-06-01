package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    // Vérifie si des paiements existent pour une réservation donnée
    boolean existsByReservationId(Long reservationId);

    // Vérifie les paiements actifs par événement et statut
    boolean existsByReservation_Evenement_IdAndStatut(Long evenementId, String statut);

    // Récupère tous les paiements liés à une réservation
    List<Paiement> findByReservation(Reservation reservation);
}
