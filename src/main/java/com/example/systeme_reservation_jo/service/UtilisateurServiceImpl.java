package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.dto.UtilisateurDTO;
import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Retourne la liste complète des utilisateurs
    @Override
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    // Retourne uniquement les utilisateurs ayant le rôle administrateur
    @Override
    public List<Utilisateur> getAllAdminUsers() {
        return utilisateurRepository.findByRoles_Name("ROLE_ADMINISTRATEUR");
    }

    // Vérifie si un utilisateur existe par email
    @Override
    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    // Recherche un utilisateur par son email
    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return utilisateurRepository.findByEmail(email);
    }

    // Sauvegarde un nouvel utilisateur avec encodage du mot de passe si nécessaire
    @Override
    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getPassword() != null
                && !utilisateur.getPassword().isBlank()
                && !utilisateur.getPassword().startsWith("$2a$")) {
            utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        }
        return utilisateurRepository.save(utilisateur);
    }

    // Mise à jour des informations d'un utilisateur existant identifié par son id
    // Ici, on met à jour le nom d'utilisateur, l'email, et on encode le mot de passe s'il est fourni.
    @Override
    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);
        if (utilisateurOpt.isEmpty()) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        Utilisateur utilisateur = utilisateurOpt.get();
        utilisateur.setUsername(utilisateurDetails.getUsername());
        utilisateur.setEmail(utilisateurDetails.getEmail());

        if (utilisateurDetails.getPassword() != null && !utilisateurDetails.getPassword().isBlank()) {
            // Si le mot de passe n'est pas déjà encodé, on l'encode
            if (!utilisateurDetails.getPassword().startsWith("$2a$")) {
                utilisateur.setPassword(passwordEncoder.encode(utilisateurDetails.getPassword()));
            } else {
                utilisateur.setPassword(utilisateurDetails.getPassword());
            }
        }

        // Mise à jour éventuelle des rôles (si nécessaire)
        utilisateur.setRoles(utilisateurDetails.getRoles());
        return utilisateurRepository.save(utilisateur);
    }

    // Supprime un utilisateur par son identifiant
    @Override
    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    // Récupère un utilisateur par son id
    @Override
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }
}
