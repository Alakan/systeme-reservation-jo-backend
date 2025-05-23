package com.example.systeme_reservation_jo.service;

import com.example.systeme_reservation_jo.model.Role;
import com.example.systeme_reservation_jo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Trouver un rôle par son nom
    public Optional<Role> findByName(String roleName) {
        return roleRepository.findByName(roleName);
    }

    // Ajouter un nouveau rôle
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

}
