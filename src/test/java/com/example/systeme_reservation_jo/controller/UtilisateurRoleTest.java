package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.model.Role;
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
import java.util.Set;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UtilisateurRoleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

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
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/admin")
                        .with(user(new User("adminUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMINISTRATEUR"))))))
                .andExpect(MockMvcResultMatchers.status().isOk()); // ✅ Vérification correcte du statut 200 OK
    }

    @Test
    void accessProtectedEndpoint_AsUtilisateur_ReturnsForbidden() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/utilisateurs/admin")
                        .with(user(new User("standardUser", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_UTILISATEUR"))))))
                .andExpect(MockMvcResultMatchers.status().isForbidden()); // ✅ Vérification correcte de 403 Forbidden
   }
}
