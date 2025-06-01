package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.controller.UtilisateurController;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import com.example.systeme_reservation_jo.service.UtilisateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import java.util.Collections;
import java.util.Optional;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(UtilisateurController.class)
public class UtilisateurRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UtilisateurService utilisateurService;

    @MockBean
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(new TestingAuthenticationProvider()));
        }
    }

    @Test
    void accessProtectedEndpoint_AsAdmin_ReturnsOk() throws Exception {
        // Stubbing pour simuler le retour d'un utilisateur administrateur
        Utilisateur adminUser = new Utilisateur();
        adminUser.setId(1L);
        adminUser.setEmail("admin@example.com");
        adminUser.setUsername("adminUser");
        Mockito.when(utilisateurService.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(adminUser));

        // On utilise l'endpoint existant /api/utilisateurs/me
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/me")
                        .with(user(new User("adminUser", "password",
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"))))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void accessProtectedEndpoint_AsUtilisateur_ReturnsOk() throws Exception {
        // Stubbing pour simuler le retour d'un utilisateur standard
        Utilisateur standardUser = new Utilisateur();
        standardUser.setId(2L);
        standardUser.setEmail("standard@example.com");
        standardUser.setUsername("standardUser");
        Mockito.when(utilisateurService.findByEmail(Mockito.anyString()))
                .thenReturn(Optional.of(standardUser));

        // Comme l'endpoint /api/utilisateurs/me renvoie le profil de l'utilisateur authentifié,
        // un utilisateur standard doit également obtenir un statut 200.
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/me")
                        .with(user(new User("standardUser", "password",
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_UTILISATEUR"))))))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
