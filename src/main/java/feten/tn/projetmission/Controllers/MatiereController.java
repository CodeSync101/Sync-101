package feten.tn.projetmission.Controllers;


import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Services.MatiereService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/matiere")
@CrossOrigin("*")
public class MatiereController {

    private final MatiereService matiereService;

    public MatiereController(MatiereService matiereService) {
        this.matiereService = matiereService;
    }

    @GetMapping("/getAll")
    public List<Matiere> getAllMatieres() {
        return matiereService.getAllMatieres();
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getMatiereById(@PathVariable Long id) {
        return matiereService.getMatiereById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Matiere> createMatiere(@RequestBody Matiere matiere) {
        return ResponseEntity.ok(matiereService.saveMatiere(matiere));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMatiere(@PathVariable Long id, @RequestBody Matiere matiere) {
        return matiereService.updateMatiere(id, matiere);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteMatiere(@PathVariable Long id) {
        matiereService.deleteMatiere(id);
        return ResponseEntity.noContent().build();
    }
}
