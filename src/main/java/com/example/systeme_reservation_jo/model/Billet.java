package com.example.systeme_reservation_jo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "billets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Billet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evenement_id", insertable = false, updatable = false)
    private Long evenementId; // 🔹 Stocke uniquement l'ID de l'événement

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @NotNull(message = "Le billet doit être lié à une réservation")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    private LocalDateTime dateReservation;

    @Column(unique = true)
    private String numeroBillet;

    @NotNull(message = "Le statut du billet ne peut pas être nul")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBillet statut;

    @NotNull(message = "Le type de billet ne peut pas être nul")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBillet type;
}
