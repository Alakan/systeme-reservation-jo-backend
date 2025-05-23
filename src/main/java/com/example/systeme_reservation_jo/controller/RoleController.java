package com.example.systeme_reservation_jo.controller;

import com.example.systeme_reservation_jo.model.Role;
import com.example.systeme_reservation_jo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Endpoint pour récupérer tous les rôles
    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Endpoint pour ajouter un rôle
    @PostMapping
    public ResponseEntity<Role> addRole(@RequestBody Role role) {
        Role savedRole = roleService.saveRole(role);
        return ResponseEntity.status(201).body(savedRole);
    }
}
