package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doNothing;
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
import java.util.Map;
import java.util.Optional;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class EvenementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvenementService evenementService;

    // Méthode utilitaire permettant d'instancier un Evenement actif
    private Evenement createValidEvenement() {
        // Paramètres : id, titre, description, dateEvenement, lieu, capaciteTotale, placesRestantes, categorie, prix, actif
        return new Evenement(1L, "Nom Evenement", "Description",
                LocalDateTime.now().plusDays(1), "Lieu", 100, 90, "Sport", BigDecimal.TEN, true);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"})
    void getEvenementById_ExistingId_ReturnsEvenement() throws Exception {
        Long id = 1L;
        Evenement evenement = createValidEvenement();
        Mockito.when(evenementService.getEvenementById(id)).thenReturn(Optional.of(evenement));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                // Vérifie que la propriété "titre" vaut "Nom Evenement"
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Evenement"));
    }

    // Pour désactiver un événement (au lieu de le supprimer définitivement),
    // on utilise le mapping PUT /api/evenements/{id}/desactiver
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"})
    void desactiverEvenement_ExistingId_ReturnsUpdatedEvenement() throws Exception {
        Long evenementId = 1L;
        Evenement evenement = createValidEvenement();
        // Création d'un événement désactivé (actif passe à false)
        Evenement deactivatedEvenement = new Evenement(
                evenement.getId(),
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getDateEvenement(),
                evenement.getLieu(),
                evenement.getCapaciteTotale(),
                evenement.getPlacesRestantes(),
                evenement.getCategorie(),
                evenement.getPrix(),
                false
        );
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.of(evenement));
        Mockito.when(evenementService.desactiverEvenement(evenementId)).thenReturn(deactivatedEvenement);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/" + evenementId + "/desactiver"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.actif").value(false));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"})
    void desactiverEvenement_NonExistingId_ReturnsNotFound() throws Exception {
        Long evenementId = 2L;
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/" + evenementId + "/desactiver"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"})
    void updateEvenement_ValidEvenement_ReturnsUpdatedEvenement() throws Exception {
        Long id = 1L;
        Evenement existingEvenement = createValidEvenement();
        // Création de l'événement mis à jour avec un nouveau titre et des valeurs modifiées.
        Evenement updatedEvenement = new Evenement(
                1L,
                "Nom Modifié",
                "Description",
                LocalDateTime.now().plusDays(2),
                "Autre Lieu",
                120,
                100,
                "Autre Sport",
                BigDecimal.valueOf(20),
                true
        );
        Mockito.when(evenementService.getEvenementById(id)).thenReturn(Optional.of(existingEvenement));
        Mockito.when(evenementService.updateEvenement(Mockito.eq(id), Mockito.any(Evenement.class)))
                .thenReturn(updatedEvenement);

        // Convertir l'objet updatedEvenement en Map et retirer la propriété "evenementId" pour éviter un conflit lors de la désérialisation.
        Map<String, Object> updatePayload = objectMapper.convertValue(updatedEvenement, Map.class);
        updatePayload.remove("evenementId");
        String jsonPayload = objectMapper.writeValueAsString(updatePayload);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                // Vérifie que la propriété "titre" vaut "Nom Modifié"
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Modifié"));
    }
}
