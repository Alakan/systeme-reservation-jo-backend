package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findByTitre(String titre);

    List<Evenement> findByTitreContainingIgnoreCase(String motCle);

    List<Evenement> findByDescriptionContainingIgnoreCase(String motCle);

    List<Evenement> findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String titre, String description);

    @Query("SELECT e FROM Evenement e WHERE e.dateEvenement BETWEEN :start AND :end")
    List<Evenement> findEvenementsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    // Pour récupérer uniquement les événements actifs
    List<Evenement> findByActifTrue();

    // La méthode existante pour supprimer les réservations associées reste inchangée
    @Modifying
    @Transactional
    @Query("DELETE FROM Reservation r WHERE r.evenement.id = :eventId")
    void deleteReservationsByEvenement(@Param("eventId") Long eventId);
}
