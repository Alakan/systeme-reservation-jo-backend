package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaiementRepository extends JpaRepository<Paiement, Long> {

    boolean existsByReservationId(Long reservationId); // ✅ Vérifie si des paiements existent pour une réservation

    boolean existsByReservation_Evenement_IdAndStatut(Long evenementId, String statut); // Vérifie les paiements actifs

    List<Paiement> findByReservation(Reservation reservation); // Récupère les paiements liés à une réservation
}
