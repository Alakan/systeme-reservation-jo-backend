package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.Billet;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.repository.EvenementRepository;
import com.example.systeme_reservation_jo.repository.BilletRepository;
import com.example.systeme_reservation_jo.repository.PaiementRepository;
import com.example.systeme_reservation_jo.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvenementServiceImpl implements EvenementService {

    private final EvenementRepository evenementRepository;
    private final BilletRepository billetRepository;
    private final PaiementRepository paiementRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public EvenementServiceImpl(EvenementRepository evenementRepository,
                                BilletRepository billetRepository,
                                PaiementRepository paiementRepository,
                                ReservationRepository reservationRepository) {
        this.evenementRepository = evenementRepository;
        this.billetRepository = billetRepository;
        this.paiementRepository = paiementRepository;
        this.reservationRepository = reservationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> getAllEvenements() {
        return evenementRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> getAllEvenementsPublic() {
        return evenementRepository.findByActifTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Evenement> getEvenementById(Long id) {
        return evenementRepository.findById(id);
    }

    @Override
    @Transactional
    public Evenement createEvenement(Evenement evenement) {
        if (evenement.getDateEvenement() == null || evenement.getDateEvenement().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement doit être dans le futur.");
        }
        if (evenement.getLieu() == null || evenement.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de l'événement ne peut pas être null ou vide.");
        }
        if (evenement.getCapaciteTotale() <= 0) {
            throw new IllegalArgumentException("La capacité totale doit être supérieure à zéro.");
        }
        // Le champ actif est géré par défaut dans l'entité (valeur true)
        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional
    public Evenement updateEvenement(Long id, Evenement evenementDetails) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + id));

        if (evenementDetails.getDateEvenement() == null ||
                evenementDetails.getDateEvenement().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La date de l'événement doit être dans le futur.");
        }
        if (evenementDetails.getLieu() == null || evenementDetails.getLieu().trim().isEmpty()) {
            throw new IllegalArgumentException("Le lieu de l'événement ne peut pas être vide.");
        }
        if (evenementDetails.getCapaciteTotale() <= 0) {
            throw new IllegalArgumentException("La capacité totale doit être supérieure à zéro.");
        }

        evenement.setTitre(evenementDetails.getTitre());
        evenement.setDescription(evenementDetails.getDescription());
        evenement.setDateEvenement(evenementDetails.getDateEvenement());
        evenement.setLieu(evenementDetails.getLieu());
        evenement.setCapaciteTotale(evenementDetails.getCapaciteTotale());
        evenement.setPlacesRestantes(evenementDetails.getPlacesRestantes());
        evenement.setCategorie(evenementDetails.getCategorie());
        evenement.setPrix(evenementDetails.getPrix());

        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> searchEvenements(String motCle) {
        return evenementRepository.findByTitreContainingIgnoreCaseOrDescriptionContainingIgnoreCase(motCle, motCle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Evenement> findEvenementsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return evenementRepository.findEvenementsBetweenDates(start, end);
    }

    @Override
    @Transactional
    public Evenement desactiverEvenement(Long id) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + id));
        evenement.setActif(false);
        return evenementRepository.save(evenement);
    }

    @Override
    @Transactional
    public Evenement reactiverEvenement(Long id) {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Événement non trouvé avec l'id : " + id));
        evenement.setActif(true);
        return evenementRepository.save(evenement);
    }
}
