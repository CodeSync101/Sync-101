package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.services.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "*")
public class ReclamationController {

    @Autowired
    private ReclamationService reclamationService;

    @PostMapping("/add/{userId}")
    public Reclamation ajouterReclamation(@RequestBody Reclamation reclamation, @PathVariable Long userId) {
        return reclamationService.ajouterReclamation(reclamation, userId);
    }

    @PutMapping("/update/{id}")
    public Reclamation modifierReclamation(@PathVariable Long id, @RequestBody Reclamation updatedReclamation) {
        return reclamationService.modifierReclamation(id, updatedReclamation);
    }

    @DeleteMapping("/delete/{id}")
    public void supprimerReclamation(@PathVariable Long id) {
        reclamationService.supprimerReclamation(id);
    }

    @GetMapping("/all")
    public List<Reclamation> getAllReclamations() {
        return reclamationService.getAllReclamations();
    }
}
