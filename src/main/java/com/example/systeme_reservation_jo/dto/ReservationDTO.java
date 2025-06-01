package com.example.systeme_reservation_jo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ReservationDTO {
    private Long id;
    private LocalDateTime dateReservation;
    private int nombreBillets;
    private String statut;
    private String modePaiement;

    // L'objet événement est désormais intégré sous forme d'objet DTO
    private EvenementDTO evenement;

    // Champs supplémentaires pour afficher les prix
    private BigDecimal prixUnitaire;
    private BigDecimal prixTotal;
}
