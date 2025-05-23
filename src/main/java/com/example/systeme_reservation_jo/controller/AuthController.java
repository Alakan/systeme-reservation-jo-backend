package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.BadCredentialsException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtAuthenticationResponse jwtResponse = authService.login(loginRequest);
            System.out.println("Token généré et envoyé : " + jwtResponse.getToken()); // ✅ Vérification console
            return ResponseEntity.ok(jwtResponse);
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Échec de l'authentification : Identifiants incorrects.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        return authService.register(signupRequest);
    }
}
