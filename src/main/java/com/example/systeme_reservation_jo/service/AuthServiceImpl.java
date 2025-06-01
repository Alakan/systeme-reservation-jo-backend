package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Role;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.payload.request.LoginRequest;
import com.example.systeme_reservation_jo.payload.request.SignupRequest;
import com.example.systeme_reservation_jo.payload.response.JwtAuthenticationResponse;
import com.example.systeme_reservation_jo.payload.response.MessageResponse;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import com.example.systeme_reservation_jo.repository.RoleRepository;
import com.example.systeme_reservation_jo.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Override
    public JwtAuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        logger.info("Authentification réussie pour : " + loginRequest.getEmail());
        logger.info("Token généré : " + jwt);

        return new JwtAuthenticationResponse(jwt);
    }

    @Override
    public ResponseEntity<?> register(SignupRequest signupRequest) {
        if (utilisateurRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: Le nom d'utilisateur est déjà pris!"));
        }

        if (utilisateurRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Erreur: L'email est déjà utilisé!"));
        }

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setUsername(signupRequest.getUsername());
        utilisateur.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        utilisateur.setEmail(signupRequest.getEmail());

        // Vérifier si les rôles existent avant de les assigner
        Set<String> roleNames = new HashSet<>();
        roleNames.add("ROLE_UTILISATEUR");

        Set<Role> roles = roleNames.stream()
                .map(roleName -> {
                    Optional<Role> roleOpt = roleRepository.findByName(roleName);
                    if (roleOpt.isPresent()) {
                        return roleOpt.get();
                    } else {
                        logger.warn("Rôle non trouvé : " + roleName);
                        throw new RuntimeException("Rôle non trouvé : " + roleName);
                    }
                })
                .collect(Collectors.toSet());

        utilisateur.setRoles(roles);
        utilisateurRepository.save(utilisateur);

        logger.info("Utilisateur enregistré avec succès : " + utilisateur.getEmail());

        return ResponseEntity.ok(new MessageResponse("Utilisateur enregistré avec succès!"));
    }
}
