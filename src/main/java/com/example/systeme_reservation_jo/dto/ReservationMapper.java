package com.example.systeme_reservation_jo.dto;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import java.math.BigDecimal;

public class ReservationMapper {
    public static ReservationDTO toDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setId(reservation.getId());
        dto.setDateReservation(reservation.getDateReservation());
        dto.setNombreBillets(reservation.getNombreBillets());
        dto.setStatut(reservation.getStatut().name());
        dto.setModePaiement(reservation.getModePaiement() != null ? reservation.getModePaiement().name() : null);

        Evenement ev = reservation.getEvenement();
        if (ev != null) {
            EvenementDTO evDto = new EvenementDTO();
            evDto.setId(ev.getId());
            evDto.setTitre(ev.getTitre());
            evDto.setDescription(ev.getDescription());
            evDto.setDateEvenement(ev.getDateEvenement());
            evDto.setLieu(ev.getLieu());
            evDto.setPrix(ev.getPrix()); // Récupération du prix unitaire depuis l'événement
            dto.setEvenement(evDto);

            // Calcul du prix unitaire et prix total
            dto.setPrixUnitaire(ev.getPrix());
            // Le prix total = prix unitaire multiplié par le nombre de billets réservés
            dto.setPrixTotal(ev.getPrix().multiply(BigDecimal.valueOf(reservation.getNombreBillets())));
        }
        return dto;
    }
}
