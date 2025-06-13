package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Matiere;
import com.example.GestionRec_Not.services.MatiereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matieres")
@CrossOrigin(origins = "http://localhost:4200")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    @PostMapping("/add")
    public ResponseEntity<Matiere> ajouterMatiere(@RequestBody Matiere matiere) {
        return ResponseEntity.ok(matiereService.ajouterMatiere(matiere));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Matiere> modifierMatiere(@PathVariable Long id, @RequestBody Matiere matiere) {
        return ResponseEntity.ok(matiereService.modifierMatiere(id, matiere));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> supprimerMatiere(@PathVariable Long id) {
        matiereService.supprimerMatiere(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Matiere>> getAllMatieres() {
        return ResponseEntity.ok(matiereService.getAllMatieres());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Matiere> getMatiereById(@PathVariable Long id) {
        return ResponseEntity.ok(matiereService.getMatiereById(id));
    }

    @GetMapping("/nom/{nomMatiere}")
    public ResponseEntity<Matiere> getByNomMatiere(@PathVariable String nomMatiere) {
        return ResponseEntity.ok(matiereService.getByNomMatiere(nomMatiere));
    }
    
    @PostMapping("/{matiereId}/assigner-etudiant/{etudiantId}")
    public ResponseEntity<Matiere> assignerEtudiantAMatiere(
            @PathVariable Long matiereId, 
            @PathVariable Long etudiantId) {
        return ResponseEntity.ok(matiereService.assignerEtudiantAMatiere(matiereId, etudiantId));
    }
    
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Matiere>> getMatieresByEtudiantId(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(matiereService.getMatieresByEtudiantId(etudiantId));
    }
} 