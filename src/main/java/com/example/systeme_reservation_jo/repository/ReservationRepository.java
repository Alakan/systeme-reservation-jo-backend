package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByEvenement_Id(Long evenementId);

    @Query("SELECT r FROM Reservation r LEFT JOIN FETCH r.evenement WHERE r.utilisateur.id = :utilisateurId AND r.evenement IS NOT NULL")
    List<Reservation> findReservationsByUtilisateurId(@Param("utilisateurId") Long utilisateurId);

    List<Reservation> findByStatut(StatutReservation statut);

    List<Reservation> findByUtilisateur_IdAndStatut(Long utilisateurId, StatutReservation statut);

    // Pour récupérer uniquement les réservations actives
    List<Reservation> findByActifTrue();
}
