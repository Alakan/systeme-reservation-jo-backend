package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import com.example.systeme_reservation_jo.repository.PaiementRepository;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaiementRepository paiementRepository;
    private final BilletRepository billetRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  PaiementRepository paiementRepository,
                                  BilletRepository billetRepository) {
        this.reservationRepository = reservationRepository;
        this.paiementRepository = paiementRepository;
        this.billetRepository = billetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    @Transactional
    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation updateReservation(Long id, Reservation reservationDetails) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));

        reservation.setUtilisateur(reservationDetails.getUtilisateur());
        reservation.setDateReservation(reservationDetails.getDateReservation());
        reservation.setNombreBillets(reservationDetails.getNombreBillets());
        reservation.setStatut(reservationDetails.getStatut());
        reservation.setEvenement(reservationDetails.getEvenement());

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void deleteReservation(Long id) throws EntityNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));
        reservationRepository.delete(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByUtilisateur(Long utilisateurId) {
        return reservationRepository.findReservationsByUtilisateurId(utilisateurId);
    }

    @Override
    @Transactional
    public Reservation effectuerPaiement(Long id, ModePaiement modePaiement) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable avec l'id : " + id));

        if (reservation.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new IllegalStateException("La réservation est déjà payée ou annulée.");
        }

        // Mise à jour de la réservation : affectation du mode de paiement et changement du statut
        reservation.setModePaiement(modePaiement);
        reservation.setStatut(StatutReservation.CONFIRMEE);
        Reservation reservationConfirmee = reservationRepository.save(reservation);

        // Création du paiement associé
        Paiement paiement = new Paiement();
        paiement.setReservation(reservationConfirmee);
        paiement.setMethodePaiement(modePaiement);
        paiement.setDatePaiement(LocalDateTime.now());
        BigDecimal montant = reservationConfirmee.getEvenement().getPrix()
                .multiply(BigDecimal.valueOf(reservationConfirmee.getNombreBillets()));
        paiement.setMontant(montant);
        paiement.setStatut("SUCCES");
        paiementRepository.save(paiement);

        // Création et enregistrement du billet associé
        Billet billet = new Billet();
        billet.setReservation(reservationConfirmee);
        billet.setEvenement(reservationConfirmee.getEvenement());
        billet.setDateReservation(LocalDateTime.now());
        billet.setNumeroBillet(generateUniqueCode());
        billet.setStatut(com.example.systeme_reservation_jo.model.StatutBillet.VALIDE);
        billet.setType(com.example.systeme_reservation_jo.model.TypeBillet.ADULTE);

        // Calcul du prix total du billet (prix unitaire * nombre de billets)
        BigDecimal prixTotal = reservationConfirmee.getEvenement().getPrix()
                .multiply(BigDecimal.valueOf(reservationConfirmee.getNombreBillets()));
        billet.setPrixTotal(prixTotal);

        billetRepository.save(billet);


        return reservationConfirmee;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reservation> findReservationsByStatut(StatutReservation statut) {
        return reservationRepository.findByStatut(statut);
    }

    @Override
    @Transactional
    public Reservation cancelReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation introuvable avec l'id : " + id));

        if (reservation.getStatut() == StatutReservation.CONFIRMEE) {
            throw new IllegalStateException("Impossible d'annuler une réservation déjà confirmée.");
        }
        reservation.setStatut(StatutReservation.ANNULEE);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation desactiverReservation(Long id) throws EntityNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));
        reservation.setActif(false);
        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public Reservation reactiverReservation(Long id) throws EntityNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Réservation non trouvée avec l'id : " + id));
        reservation.setActif(true);
        return reservationRepository.save(reservation);
    }

    // Méthode utilitaire pour générer un code de billet unique
    private String generateUniqueCode() {
        return "BILLET-" + System.currentTimeMillis();
    }
}
