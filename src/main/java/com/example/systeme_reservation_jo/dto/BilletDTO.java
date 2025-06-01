package com.example.systeme_reservation_jo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class BilletDTO {
    private Long id;
    private String numeroBillet;
    private LocalDateTime dateReservation;
    private String statut;
    private String type;
    private EvenementDTO evenement;
    private BigDecimal prixTotal; // Ajout du prix total
}
