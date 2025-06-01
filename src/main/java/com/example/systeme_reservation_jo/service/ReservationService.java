package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    List<Reservation> getAllReservations();
    Optional<Reservation> getReservationById(Long id) throws EntityNotFoundException;
    Reservation createReservation(Reservation reservation);
    Reservation updateReservation(Long id, Reservation reservationDetails) throws EntityNotFoundException;
    void deleteReservation(Long id) throws EntityNotFoundException;
    List<Reservation> getReservationsByUtilisateur(Long utilisateurId);
    Reservation effectuerPaiement(Long id, ModePaiement modePaiement) throws EntityNotFoundException, IllegalStateException;
    List<Reservation> findReservationsByStatut(StatutReservation statut);
    Reservation cancelReservation(Long id) throws EntityNotFoundException, IllegalStateException;

    // Désactivation d'une réservation
    Reservation desactiverReservation(Long id) throws EntityNotFoundException;

    // Réactivation d'une réservation
    Reservation reactiverReservation(Long id) throws EntityNotFoundException;
}
