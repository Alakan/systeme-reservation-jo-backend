package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Evenement;
import com.example.systeme_reservation_jo.service.EvenementService;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private Evenement createValidEvenement() {
        return new Evenement(1L, "Nom Evenement", "Description", LocalDateTime.now().plusDays(1), "Lieu", 100, 90, "Sport", BigDecimal.TEN);
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"}) // 🔹 Simulation d’un admin
    void getEvenementById_ExistingId_ReturnsEvenement() throws Exception {
        Long id = 1L;
        Evenement evenement = createValidEvenement();
        Mockito.when(evenementService.getEvenementById(id)).thenReturn(Optional.of(evenement));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/evenements/" + id))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Evenement"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"}) // 🔹 Simulation d’un admin
    void deleteEvenement_ExistingId_ReturnsNoContent() throws Exception {
        Long evenementId = 1L;
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.of(createValidEvenement()));
        Mockito.doNothing().when(evenementService).deleteEvenement(evenementId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/evenements/" + evenementId))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"}) // 🔹 Simulation d’un admin
    void deleteEvenement_NonExistingId_ReturnsNotFound() throws Exception {
        Long evenementId = 2L;
        Mockito.when(evenementService.getEvenementById(evenementId)).thenReturn(Optional.empty());
        Mockito.doNothing().when(evenementService).deleteEvenement(evenementId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/evenements/" + evenementId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRATEUR"}) // 🔹 Simulation d’un admin
    void updateEvenement_ValidEvenement_ReturnsUpdatedEvenement() throws Exception {
        Long id = 1L;
        Evenement existingEvenement = createValidEvenement();
        Evenement updatedEvenement = new Evenement(1L, "Nom Modifié", "Description", LocalDateTime.now().plusDays(2), "Autre Lieu", 120, 100, "Autre Sport", BigDecimal.valueOf(20));
        Mockito.when(evenementService.getEvenementById(id)).thenReturn(Optional.of(existingEvenement));
        Mockito.when(evenementService.updateEvenement(Mockito.eq(id), Mockito.any(Evenement.class))).thenReturn(updatedEvenement);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/evenements/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedEvenement)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.titre").value("Nom Modifié"));
    }
}
