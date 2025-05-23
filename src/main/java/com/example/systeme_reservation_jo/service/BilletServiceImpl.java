package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutBillet;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BilletServiceImpl implements BilletService {

    private static final Logger logger = LoggerFactory.getLogger(BilletServiceImpl.class);

    @Autowired
    private BilletRepository billetRepository;

    @Autowired
    private EvenementRepository evenementRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Override
    public Billet saveBillet(Billet billet) {
        logger.info("Tentative de création d’un billet avec : {}", billet);

        if (billet.getEvenementId() == null) {
            throw new IllegalArgumentException("L'événement du billet ne peut pas être null.");
        }
        if (billet.getReservation() == null || billet.getReservation().getId() == null) {
            throw new IllegalArgumentException("La réservation du billet ne peut pas être null.");
        }

        Evenement evenement = evenementRepository.findById(billet.getEvenementId())
                .orElseThrow(() -> new RuntimeException("Événement introuvable avec ID : " + billet.getEvenementId()));
        Reservation reservation = reservationRepository.findById(billet.getReservation().getId())
                .orElseThrow(() -> new RuntimeException("Réservation introuvable avec ID : " + billet.getReservation().getId()));

        billet.setEvenement(evenement);
        billet.setReservation(reservation);

        if (billet.getNumeroBillet() == null || billetRepository.existsByNumeroBillet(billet.getNumeroBillet())) {
            billet.setNumeroBillet("JO-" + System.currentTimeMillis());
        }

        logger.info("Enregistrement du billet : {}", billet);
        return billetRepository.save(billet);
    }

    @Override
    public List<Billet> getAllBillets() {
        logger.info("Récupération de tous les billets.");
        return billetRepository.findAll();
    }

    @Override
    public Optional<Billet> getBilletById(Long id) {
        logger.info("Recherche du billet avec ID : {}", id);
        return billetRepository.findById(id);
    }

    @Override
    public List<Billet> getBilletsByEvenement(Evenement evenement) {
        logger.info("Récupération des billets pour l'événement ID : {}", evenement.getId());
        return billetRepository.findByEvenement_Id(evenement.getId());
    }

    @Override
    public List<Billet> getBilletsByUtilisateur(Utilisateur utilisateur) {
        logger.info("Récupération des billets pour l'utilisateur ID : {}", utilisateur.getId());
        return billetRepository.findByUtilisateur(utilisateur);
    }

    @Override
    public List<Billet> getBilletsByStatut(StatutBillet statut) {
        logger.info("Récupération des billets avec le statut : {}", statut);
        return billetRepository.findByStatut(statut);
    }

    @Override
    public boolean existsByNumeroBillet(String numeroBillet) {
        return billetRepository.existsByNumeroBillet(numeroBillet);
    }

    @Override
    public Billet updateBillet(Long id, Billet billetDetails) {
        logger.info("Tentative de mise à jour du billet ID : {}", id);

        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Billet non trouvé avec l'ID : " + id));

        billet.setEvenement(billetDetails.getEvenement());
        billet.setUtilisateur(billetDetails.getUtilisateur());
        billet.setDateReservation(billetDetails.getDateReservation());
        billet.setStatut(billetDetails.getStatut());
        billet.setType(billetDetails.getType());

        logger.info("Billet mis à jour avec ID : {}", id);
        return billetRepository.save(billet);
    }

    @Override
    public void deleteBillet(Long id) {
        logger.info("Suppression du billet avec ID : {}", id);

        if (!billetRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Billet introuvable avec l'ID : " + id);
        }

        billetRepository.deleteById(id);
        logger.info("Billet supprimé avec succès.");
    }
}
