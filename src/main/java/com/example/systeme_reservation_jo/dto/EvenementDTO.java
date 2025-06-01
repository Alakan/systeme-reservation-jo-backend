package com.example.systeme_reservation_jo.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class EvenementDTO {
    private Long id;
    private String titre;
    private String description;
    private LocalDateTime dateEvenement;
    private String lieu;
    private BigDecimal prix; // Champ ajouté pour afficher le prix unitaire
}
