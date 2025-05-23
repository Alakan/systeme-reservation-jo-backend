package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Paiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.repository.PaiementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaiementServiceImpl implements PaiementService {

    @Autowired
    private PaiementRepository paiementRepository;

    @Override
    public Paiement savePaiement(Paiement paiement) {
        return paiementRepository.save(paiement);
    }

    @Override
    public List<Paiement> getAllPaiements() {
        return paiementRepository.findAll();
    }

    @Override
    public Optional<Paiement> getPaiementById(Long id) {
        return paiementRepository.findById(id);
    }

    @Override
    public Paiement updatePaiement(Long id, Paiement paiementDetails) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id : " + id));

        paiement.setReservation(paiementDetails.getReservation());
        paiement.setMontant(paiementDetails.getMontant());
        paiement.setDatePaiement(paiementDetails.getDatePaiement());
        paiement.setMethodePaiement(paiementDetails.getMethodePaiement());
        paiement.setTransactionId(paiementDetails.getTransactionId());
        paiement.setStatut(paiementDetails.getStatut());

        return paiementRepository.save(paiement);
    }

    @Override
    public void deletePaiement(Long id) {
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id : " + id));

        if (paiementRepository.existsByReservationId(paiement.getReservation().getId())) {
            throw new IllegalStateException("Impossible de supprimer le paiement : il est lié à une réservation.");
        }

        paiementRepository.deleteById(id);
    }

    @Override
    public List<Paiement> getPaiementsByReservation(Reservation reservation) {
        return paiementRepository.findByReservation(reservation);
    }
}
