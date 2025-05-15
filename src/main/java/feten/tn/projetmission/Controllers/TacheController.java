package feten.tn.projetmission.Controllers;
import feten.tn.projetmission.Entities.DTO.RequestTacheDTO;
import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Entities.Tache;
import feten.tn.projetmission.Services.MatiereService;
import feten.tn.projetmission.Services.TacheService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/tache")
public class TacheController {
    private final TacheService tacheService;
    private final MatiereService matiereService;
    @GetMapping("/getAll")
    public List<Tache> getAllTaches() {
        return tacheService.getAllTaches();
    }

    @GetMapping("/getTache/{id}")
    public Tache getTache(@PathVariable Long id) {
        return tacheService.getTacheById(id)
                .orElseThrow(() -> new EntityNotFoundException("Requested tache not found"));
    }

    @GetMapping("/getTacheByMatiere/{idMat}")
    public List<Tache> getTacheByMatiere(@PathVariable Long idMat) {
        Matiere matiere = matiereService.getMatiereById(idMat).orElse(null);
        return tacheService.findByMat(matiere);
    }

    @PostMapping("/createTache")
    public ResponseEntity<?> ajouterTache(@RequestBody RequestTacheDTO requestTacheDTO) {
        Matiere matiere = matiereService.getMatiereById(requestTacheDTO.getMatiereId()).orElse(null);
        if(matiere == null){
            return ResponseEntity.badRequest().body("tache non trouvé");
        }
        Tache tache = new Tache();
        tache.setTitre(requestTacheDTO.getTitre());
        tache.setStatus(requestTacheDTO.getStatus());
        tache.setNotetache(requestTacheDTO.getNotetache());
        tache.setMatiere(matiere);
        return ResponseEntity.ok(tacheService.save(tache));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateTache(@RequestBody Tache tache, @PathVariable Long id) {
        if (tacheService.existById(id)) {
            Tache existingTache = tacheService.getTacheById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Requested tache not found"));
            existingTache.setTitre(tache.getTitre());
            existingTache.setNotetache(tache.getNotetache());
            existingTache.setStatus(tache.getStatus());
            tacheService.save(existingTache);
            return ResponseEntity.ok().body(existingTache);
        } else {
            HashMap<String, String> message = new HashMap<>();
            message.put("message", id + "Tache not found or matched");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTache(@PathVariable Long id) {
        if (tacheService.existById(id)) {
            tacheService.deleteTache(id);
            HashMap<String, String> message = new HashMap<>();
            message.put("message", "Tache with id " + id + " deleted successfully");
            return ResponseEntity.ok().body(message);
        } else {
            HashMap<String, String> message = new HashMap<>();
            message.put("message", id + " Tache not found or matched");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
    }

}
