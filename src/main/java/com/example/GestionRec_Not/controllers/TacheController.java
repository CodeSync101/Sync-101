package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Tache;
import com.example.GestionRec_Not.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
@CrossOrigin(origins = "http://localhost:4200")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @PostMapping("/add/{userId}/{matiereId}")
    public ResponseEntity<Tache> ajouterTache(
            @RequestBody Tache tache,
            @PathVariable Long userId,
            @PathVariable Long matiereId) {
        Tache newTache = tacheService.ajouterTache(tache, userId, matiereId);
        return new ResponseEntity<>(newTache, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tache> modifierTache(
            @PathVariable Long id,
            @RequestBody Tache tache) {
        Tache updatedTache = tacheService.modifierTache(id, tache);
        return new ResponseEntity<>(updatedTache, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id) {
        tacheService.supprimerTache(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Tache>> getAllTaches() {
        List<Tache> taches = tacheService.getAllTaches();
        return new ResponseEntity<>(taches, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tache> getTacheById(@PathVariable Long id) {
        Tache tache = tacheService.getTacheById(id);
        return new ResponseEntity<>(tache, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Tache>> getTachesByUser(@PathVariable Long userId) {
        List<Tache> taches = tacheService.getTachesByUser(userId);
        return new ResponseEntity<>(taches, HttpStatus.OK);
    }

    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<Tache>> getTachesByMatiere(@PathVariable Long matiereId) {
        List<Tache> taches = tacheService.getTachesByMatiere(matiereId);
        return new ResponseEntity<>(taches, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/matiere/{matiereId}")
    public ResponseEntity<List<Tache>> getTachesByUserAndMatiere(
            @PathVariable Long userId,
            @PathVariable Long matiereId) {
        List<Tache> taches = tacheService.getTachesByUserAndMatiere(userId, matiereId);
        return new ResponseEntity<>(taches, HttpStatus.OK);
    }

    @GetMapping("/terminees/{terminee}")
    public ResponseEntity<List<Tache>> getTachesTerminees(@PathVariable Boolean terminee) {
        List<Tache> taches = tacheService.getTachesTerminees(terminee);
        return new ResponseEntity<>(taches, HttpStatus.OK);
    }

    @PutMapping("/{id}/terminer/{terminee}")
    public ResponseEntity<Tache> marquerCommeTerminee(
            @PathVariable Long id,
            @PathVariable Boolean terminee) {
        Tache tache = tacheService.marquerCommeTerminee(id, terminee);
        return new ResponseEntity<>(tache, HttpStatus.OK);
    }
} 