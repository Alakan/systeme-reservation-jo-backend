package com.example.systeme_reservation_jo.dto;

import com.example.systeme_reservation_jo.model.Billet;

public class BilletMapper {
    public static BilletDTO toDTO(Billet billet) {
        BilletDTO dto = new BilletDTO();
        dto.setId(billet.getId());
        dto.setNumeroBillet(billet.getNumeroBillet());
        dto.setDateReservation(billet.getDateReservation());
        dto.setStatut(billet.getStatut().name());
        dto.setType(billet.getType().name());
        // Mapper l'objet événement via EvenementMapper
        dto.setEvenement(EvenementMapper.toDTO(billet.getEvenement()));
        dto.setPrixTotal(billet.getPrixTotal());
        return dto;
    }
}
