package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Utilisateur;
import java.util.List;
import java.util.Optional;

public interface UtilisateurService {

    // Retourne la liste complète des utilisateurs
    List<Utilisateur> getAllUtilisateurs();

    // Retourne la liste des utilisateurs ayant le rôle d'administrateur
    List<Utilisateur> getAllAdminUsers();

    // Vérifie si un utilisateur existe grâce à son email
    boolean existsByEmail(String email);

    // Recherche un utilisateur par son email
    Optional<Utilisateur> findByEmail(String email);

    // Sauvegarde un nouvel utilisateur (avec encodage du mot de passe si nécessaire)
    Utilisateur saveUtilisateur(Utilisateur utilisateur);

    // Met à jour les informations d'un utilisateur existant identifié par son id
    Utilisateur updateUtilisateur(Long id, Utilisateur utilisateurDetails);

    // Supprime un utilisateur identifié par son id
    void deleteUtilisateur(Long id);

    // Récupère un utilisateur par son id
    Optional<Utilisateur> getUtilisateurById(Long id);
}
