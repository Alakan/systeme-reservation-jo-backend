package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutBillet;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BilletServiceImpl implements BilletService {

    private static final Logger logger = LoggerFactory.getLogger(BilletServiceImpl.class);

    private final BilletRepository billetRepository;
    private final EvenementRepository evenementRepository;
    private final ReservationRepository reservationRepository;

    public BilletServiceImpl(BilletRepository billetRepository,
                             EvenementRepository evenementRepository,
                             ReservationRepository reservationRepository) {
        this.billetRepository = billetRepository;
        this.evenementRepository = evenementRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    public Billet saveBillet(Billet billet) {
        logger.info("Tentative de création d’un billet avec : {}", billet);

        // Vérification que la réservation existe bien dans le payload
        if (billet.getReservation() == null || billet.getReservation().getId() == null) {
            throw new IllegalArgumentException("La réservation du billet ne peut pas être null.");
        }

        // Récupération de la Réservation à partir de son ID
        Reservation reservation = reservationRepository.findById(billet.getReservation().getId())
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable avec ID : " + billet.getReservation().getId()));

        // Détermination de l'objet Evenement
        Evenement evenement;
        if (billet.getEvenement() != null && billet.getEvenement().getId() != null) {
            // Si le payload fournit un objet Evenement
            evenement = evenementRepository.findById(billet.getEvenement().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Événement introuvable avec ID : " + billet.getEvenement().getId()));
        } else {
            // Sinon, on tente de récupérer l'événement depuis la réservation
            if (reservation.getEvenement() == null) {
                throw new IllegalArgumentException("Aucun événement fourni dans le payload et la réservation ne contient pas d'événement.");
            }
            evenement = reservation.getEvenement();
            logger.info("Aucun événement fourni dans le payload, utilisation de l'événement de la réservation : {}", evenement.getId());
        }

        // Affectation explicite de l'objet Evenement et de la Réservation dans le billet
        billet.setEvenement(evenement);
        billet.setReservation(reservation);

        // Optionnel : Traitement de l'utilisateur, si nécessaire.
        if (billet.getUtilisateur() != null && billet.getUtilisateur().getId() != null) {
            // Vous pouvez récupérer l'utilisateur via un repository Utilisateur si besoin.
        }

        // Vérification et génération du numéro de billet si nécessaire
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
    public Optional<Billet> getBilletByReservationId(Long reservationId) {
        logger.info("Recherche du billet pour la réservation ID : {}", reservationId);
        return billetRepository.findByReservationIdWithEvenement(reservationId);
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
        logger.info("Tentative de mise à jour du billet avec l'ID : {}", id);

        Billet billet = billetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Billet non trouvé avec l'ID : " + id));

        // Si l'événement est mis à jour, récupérez l'objet Evenement et affectez-le
        if (billetDetails.getEvenement() != null && billetDetails.getEvenement().getId() != null) {
            Evenement evenement = evenementRepository.findById(billetDetails.getEvenement().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Événement introuvable avec l'ID : " + billetDetails.getEvenement().getId()));
            billet.setEvenement(evenement);
        }

        billet.setUtilisateur(billetDetails.getUtilisateur());
        billet.setDateReservation(billetDetails.getDateReservation());
        billet.setStatut(billetDetails.getStatut());
        billet.setType(billetDetails.getType());

        logger.info("Billet mis à jour avec l'ID : {}", id);
        return billetRepository.save(billet);
    }

    @Override
    public void deleteBillet(Long id) {
        logger.info("Suppression du billet avec l'ID : {}", id);

        if (!billetRepository.existsById(id)) {
            throw new EntityNotFoundException("Impossible de supprimer : Billet introuvable avec l'ID : " + id);
        }

        billetRepository.deleteById(id);
        logger.info("Billet supprimé avec succès.");
    }
}
