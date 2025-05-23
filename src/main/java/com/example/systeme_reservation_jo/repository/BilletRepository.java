package com.example.systeme_reservation_jo.repository;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.StatutBillet;
import com.example.systeme_reservation_jo.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BilletRepository extends JpaRepository<Billet, Long> {
    List<Billet> findByEvenement_Id(Long evenementId);
    List<Billet> findByUtilisateur(Utilisateur utilisateur);
    List<Billet> findByStatut(StatutBillet statut);
    boolean existsByNumeroBillet(String numeroBillet);
}
