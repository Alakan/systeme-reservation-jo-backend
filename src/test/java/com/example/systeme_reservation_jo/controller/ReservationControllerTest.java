package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.*;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private UtilisateurService utilisateurService;

    private Reservation createValidReservation() {
        Utilisateur utilisateur = new Utilisateur(1L, "testUser", "password", "test@example.com", null, null);
        Evenement evenement = new Evenement();
        evenement.setId(1L);
        Reservation reservation = new Reservation();
        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setNombreBillets(2);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        reservation.setId(1L);
        return reservation;
    }

 /*   @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        Reservation reservation = createValidReservation();
        reservation.getUtilisateur().setEmail("test@example.com");

        Mockito.when(utilisateurService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(reservation.getUtilisateur()));
        Mockito.when(reservationService.createReservation(Mockito.any(Reservation.class))).thenReturn(reservation);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservation.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(2));
    } */

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void getReservationById_ExistingId_ReturnsReservation() throws Exception {
        Reservation reservation = createValidReservation();
        Long reservationId = reservation.getId();
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservationId));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void getReservationsByUserEmail_ValidUser_ReturnsReservations() throws Exception {
        Utilisateur utilisateur = new Utilisateur(1L, "testUser", "password", "test@example.com", null, null);
        Reservation reservation = createValidReservation();
        Mockito.when(utilisateurService.findByEmail(Mockito.anyString())).thenReturn(Optional.of(utilisateur));
        Mockito.when(reservationService.getReservationsByUtilisateur(utilisateur.getId())).thenReturn(List.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/utilisateur"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(reservation.getId()));
    }
/*
    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void updateReservation_ValidData_ReturnsUpdatedReservation() throws Exception {
        // Création d’une réservation valide
        Reservation reservation = createValidReservation();
        reservation.setNombreBillets(3);

        // Assurer que l'utilisateur et l'événement sont définis correctement
        Utilisateur utilisateur = new Utilisateur(1L, "testUser", "password", "test@example.com", null, null);
        Evenement evenement = new Evenement();
        evenement.setId(1L);

        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);

        // Simuler la mise à jour dans `ReservationService`
        Mockito.when(reservationService.updateReservation(Mockito.eq(reservation.getId()), Mockito.any(Reservation.class)))
                .thenReturn(reservation);

        // Exécuter la requête PUT avec un JSON correctement structuré
        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(3));
    }*/

/*
    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void deleteReservation_ExistingId_ReturnsNoContent() throws Exception {
        Long reservationId = 1L;
        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(createValidReservation()));
        Mockito.doNothing().when(reservationService).deleteReservation(reservationId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }*/

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void effectuerPaiement_ValidPayment_ConfirmsReservation() throws Exception {
        Long reservationId = 1L;
        ModePaiement modePaiement = ModePaiement.CARTE;
        Reservation reservation = createValidReservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        Mockito.when(reservationService.getReservationById(reservationId)).thenReturn(Optional.of(reservation));
        Mockito.when(reservationService.updateReservation(Mockito.eq(reservationId), Mockito.any(Reservation.class)))
                .thenReturn(reservation);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservationId + "/paiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modePaiement)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Paiement effectué avec succès !"));
    }
}
