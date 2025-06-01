package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.model.ModePaiement;
import com.example.systeme_reservation_jo.model.Reservation;
import com.example.systeme_reservation_jo.model.StatutReservation;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.service.ReservationService;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = com.example.systeme_reservation_jo.SystemeReservationJoApplication.class)
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

    /**
     * MixIn destiné à désactiver les annotations d’identité,
     * en particulier pour masquer le getter "evenementId" qui cause le conflit.
     */
    public abstract static class NoIdentityMixIn {
        @com.fasterxml.jackson.annotation.JsonIgnore
        abstract Long getEvenementId();
    }

    /**
     * Crée une réservation valide avec un utilisateur et un événement complet.
     * L'événement est entièrement renseigné pour éviter les erreurs de désérialisation.
     */
    private Reservation createValidReservation() {
        Utilisateur utilisateur = new Utilisateur(
                1L,
                "testUser",
                "password",
                "test@example.com",
                null,
                null
        );

        Evenement evenement = new Evenement();
        evenement.setId(1L);
        evenement.setTitre("Evenement Test");
        evenement.setDescription("Description test");
        evenement.setDateEvenement(LocalDateTime.now().plusDays(1));
        evenement.setLieu("Lieu Test");
        evenement.setCapaciteTotale(100);
        evenement.setPlacesRestantes(100);
        evenement.setCategorie("Test");
        evenement.setPrix(BigDecimal.TEN); // Valeur non null, par exemple 10
        evenement.setActif(true);

        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUtilisateur(utilisateur);
        reservation.setEvenement(evenement);
        reservation.setDateReservation(LocalDateTime.now());
        reservation.setNombreBillets(2);
        reservation.setStatut(StatutReservation.EN_ATTENTE);
        return reservation;
    }
/*
    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void createReservation_ValidReservation_ReturnsCreatedReservation() throws Exception {
        Reservation reservation = createValidReservation();
        reservation.getUtilisateur().setEmail("test@example.com");

        Mockito.when(utilisateurService.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(reservation.getUtilisateur()));
        Mockito.when(reservationService.createReservation(Mockito.any(Reservation.class)))
                .thenReturn(reservation);

        // Utilisation d'une copie de l'ObjectMapper avec un mixin qui désactive le getter "evenementId"
        ObjectMapper mapper = objectMapper.copy();
        mapper.addMixIn(Reservation.class, NoIdentityMixIn.class);
        mapper.addMixIn(Evenement.class, NoIdentityMixIn.class);
        String jsonPayload = mapper.writeValueAsString(reservation);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservation.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(2));
    }*/

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void getReservationById_ExistingId_ReturnsReservation() throws Exception {
        Reservation reservation = createValidReservation();
        Long reservationId = reservation.getId();
        Mockito.when(reservationService.getReservationById(reservationId))
                .thenReturn(Optional.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/" + reservationId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(reservationId));
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void getReservationsByUserEmail_ValidUser_ReturnsReservations() throws Exception {
        Utilisateur utilisateur = new Utilisateur(
                1L,
                "testUser",
                "password",
                "test@example.com",
                null,
                null
        );
        Reservation reservation = createValidReservation();
        Mockito.when(utilisateurService.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(utilisateur));
        Mockito.when(reservationService.getReservationsByUtilisateur(utilisateur.getId()))
                .thenReturn(List.of(reservation));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/reservations/utilisateur"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value(reservation.getId()));
    }

    /*@Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void updateReservation_ValidData_ReturnsUpdatedReservation() throws Exception {
        Reservation reservation = createValidReservation();
        // Modification pour le test de mise à jour : on modifie le nombre de billets.
        reservation.setNombreBillets(3);

        Mockito.when(reservationService.updateReservation(Mockito.eq(reservation.getId()), Mockito.any(Reservation.class)))
                .thenReturn(reservation);

        ObjectMapper mapper = objectMapper.copy();
        mapper.addMixIn(Reservation.class, NoIdentityMixIn.class);
        mapper.addMixIn(Evenement.class, NoIdentityMixIn.class);
        String jsonPayload = mapper.writeValueAsString(reservation);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.nombreBillets").value(3));
    }*/

    @Test
    @WithMockUser(username = "testUser", roles = {"UTILISATEUR"})
    void effectuerPaiement_ValidPayment_ConfirmsReservation() throws Exception {
        Long reservationId = 1L;
        ModePaiement modePaiement = ModePaiement.CARTE;
        Reservation reservation = createValidReservation();
        reservation.setStatut(StatutReservation.EN_ATTENTE);

        Mockito.when(reservationService.effectuerPaiement(reservationId, modePaiement))
                .thenReturn(reservation);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/reservations/" + reservationId + "/paiement")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(modePaiement)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Paiement effectué avec succès !"));
    }
}
