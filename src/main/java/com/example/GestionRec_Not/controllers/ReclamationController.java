package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;
import com.example.GestionRec_Not.services.ReclamationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reclamations")
@CrossOrigin(origins = "http://localhost:4200")
public class ReclamationController {

    @Autowired
    private ReclamationService reclamationService;

    @PostMapping("/add/{userId}/{matiereId}")
    public Reclamation ajouterReclamation(
            @RequestBody Reclamation reclamation, 
            @PathVariable Long userId,
            @PathVariable Long matiereId) {
        return reclamationService.ajouterReclamation(reclamation, userId, matiereId);
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
    
    @GetMapping("/{id}")
    public ResponseEntity<Reclamation> getReclamationById(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.getReclamationById(id));
    }
    
    @PutMapping("/statut/{id}/{action}")
    public Reclamation changerStatutReclamation(@PathVariable Long id, @PathVariable String action) {
        return reclamationService.changerStatutReclamation(id, action);
    }
    
    @GetMapping("/priorite/{priorite}")
    public ResponseEntity<List<Reclamation>> getReclamationsParPriorite(@PathVariable Priorite priorite) {
        return ResponseEntity.ok(reclamationService.getReclamationsParPriorite(priorite));
    }
    
    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<Reclamation>> getReclamationsParStatut(@PathVariable Statut statut) {
        return ResponseEntity.ok(reclamationService.getReclamationsParStatut(statut));
    }
    
    @GetMapping("/en-attente/anciennete")
    public ResponseEntity<List<Reclamation>> getReclamationsNonTraiteesParAnciennete() {
        return ResponseEntity.ok(reclamationService.getReclamationsNonTraiteesParAnciennete());
    }
    
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<Reclamation>> getReclamationsParMatiere(@PathVariable Long matiereId) {
        return ResponseEntity.ok(reclamationService.getReclamationsParMatiere(matiereId));
    }

    @PutMapping("/traiter/{id}")
    public ResponseEntity<Reclamation> traiterReclamation(@PathVariable Long id) {
        return ResponseEntity.ok(reclamationService.traiterReclamation(id));
    }

    @PostMapping("/add-simple/{userId}")
    public Reclamation ajouterReclamationSimple(@RequestBody Reclamation reclamation, @PathVariable Long userId) {
        return reclamationService.ajouterReclamationSimple(reclamation, userId);
    }
}
