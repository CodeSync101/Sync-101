package feten.tn.projetmission.Services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Repositories.MatiereRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MatiereService {
    public MatiereService(MatiereRepository matiereRepository) {
        this.matiereRepository = matiereRepository;
    }

    private final MatiereRepository matiereRepository;

    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    public Optional<Matiere> getMatiereById(Long id) {
        return matiereRepository.findById(id);
    }

    public Matiere saveMatiere(Matiere matiere) {
        return matiereRepository.save(matiere);
    }

    public ResponseEntity<Matiere> updateMatiere(Long id, Matiere matiereDetails) {
        return matiereRepository.findById(id).map(matiere -> {
            matiere.setLibelle(matiereDetails.getLibelle());
            matiere.setNoteMatiere(matiereDetails.getNoteMatiere());
            matiere.setDescription(matiereDetails.getDescription());
            return ResponseEntity.ok(matiereRepository.save(matiere));
        }).orElse(ResponseEntity.notFound().build());
    }


    public void deleteMatiere(Long id) {
        matiereRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return matiereRepository.existsById(id);
    }
}
