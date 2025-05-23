package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Utilisateur;
import com.example.systeme_reservation_jo.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UtilisateurServiceImpl { // ✅ Suppression de `implements UtilisateurService` si `UtilisateurService` est une classe

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurServiceImpl(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public List<Utilisateur> getAllAdminUsers() {
        return utilisateurRepository.findByRoles_Name("ROLE_ADMINISTRATEUR"); // ✅ Assure que la méthode existe bien dans `UtilisateurRepository`
    }

    public boolean existsByEmail(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // ✅ Vérification que `getUsername()` et `getEmail()` existent bien
        utilisateur.setUsername(utilisateurDetails.getUsername());
        utilisateur.setEmail(utilisateurDetails.getEmail());
        utilisateur.setPassword(utilisateurDetails.getPassword());
        utilisateur.setRoles(utilisateurDetails.getRoles());

        return utilisateurRepository.save(utilisateur);
    }

    public void deleteUtilisateur(Long id) {
        utilisateurRepository.deleteById(id);
    }

    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }
}
