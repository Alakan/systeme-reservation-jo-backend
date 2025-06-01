package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.SystemeReservationJoApplication;
import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.TestingAuthenticationProvider;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

@SpringBootTest(classes = SystemeReservationJoApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return new ProviderManager(Collections.singletonList(new TestingAuthenticationProvider()));
        }
    }

    private SignupRequest createValidSignupRequest() {
        return new SignupRequest("testUser", "test@example.com", "password");
    }

    // ------- Tests pour l'inscription -------

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_ValidSignupRequest_ReturnsOk() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<?> expectedResponse =
                ResponseEntity.ok(Collections.singletonMap("message", "Utilisateur enregistré avec succès!"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_UsernameAlreadyTaken_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<?> expectedResponse =
                ResponseEntity.badRequest().body(Collections.singletonMap("message", "Erreur: Le nom d'utilisateur est déjà pris!"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_EmailAlreadyUsed_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = createValidSignupRequest();
        ResponseEntity<?> expectedResponse =
                ResponseEntity.badRequest().body(Collections.singletonMap("message", "Erreur: L'email est déjà utilisé!"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", "invalid-email", "password");
        ResponseEntity<?> expectedResponse =
                ResponseEntity.badRequest().body(Collections.singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @SuppressWarnings("unchecked")
    void registerUser_MissingEmail_ReturnsBadRequest() throws Exception {
        SignupRequest signupRequest = new SignupRequest("testUser", null, "password");
        ResponseEntity<?> expectedResponse =
                ResponseEntity.badRequest().body(Collections.singletonMap("message", "Erreur de validation"));
        doReturn(expectedResponse).when(authService).register(Mockito.any(SignupRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // ------- Tests pour la connexion -------

    @Test
    void loginUser_ValidCredentials_ReturnsJwtToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse("fakeJwtToken");
        doReturn(jwtResponse).when(authService).login(Mockito.any(LoginRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("fakeJwtToken"));
    }

    @Test
    void loginUser_InvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrongpassword");

        doThrow(new BadCredentialsException("Authentication failed"))
                .when(authService).login(Mockito.any(LoginRequest.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string("Échec de l'authentification : Identifiants incorrects."));
    }
}
