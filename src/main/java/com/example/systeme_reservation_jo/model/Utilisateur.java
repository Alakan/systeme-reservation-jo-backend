package com.example.systeme_reservation_jo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente un utilisateur du système.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ Correction Hibernate
@Entity
@Table(name = "utilisateurs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ✅ Correction du type Long → Integer

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("utilisateur") // ✅ Empêche la sérialisation récursive
    private List<Reservation> reservations;

    @ManyToMany(fetch = FetchType.EAGER) // ✅ Chargement immédiat pour éviter les problèmes Hibernate
    @JoinTable(
            name = "utilisateur_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
