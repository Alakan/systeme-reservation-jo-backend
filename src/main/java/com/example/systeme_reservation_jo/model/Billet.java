package com.example.systeme_reservation_jo.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

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

    // Nous utilisons uniquement l'association vers l'événement.
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "evenement_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @NotNull(message = "Le billet doit être lié à une réservation")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private LocalDateTime dateReservation;

    @Column(unique = true, nullable = false)
    private String numeroBillet;

    @NotNull(message = "Le statut du billet ne peut pas être nul")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutBillet statut;

    @NotNull(message = "Le type de billet ne peut pas être nul")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeBillet type;

    @PrePersist
    protected void prePersist() {
        if (dateReservation == null) {
            dateReservation = LocalDateTime.now();
        }
        if (numeroBillet == null) {
            // Génération d'un identifiant unique pour le billet
            numeroBillet = "BILLET-" + UUID.randomUUID().toString();
        }
    }

    // Nouveau champ pour le coût total de la commande (prix unitaire * nombre de billets)
    @NotNull(message = "Le prix total du billet doit etre renseigné")
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix total doit être supérieur à 0")
    private BigDecimal prixTotal= BigDecimal.ZERO;

}
