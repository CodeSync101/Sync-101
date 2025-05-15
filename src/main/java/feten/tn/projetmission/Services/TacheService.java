package feten.tn.projetmission.Services;

import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Entities.Tache;
import feten.tn.projetmission.Repositories.TacheRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@AllArgsConstructor
public class TacheService {

    private final TacheRepository tacheRepository;


    public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
    }

    public Optional<Tache> getTacheById(Long id) {
        return tacheRepository.findById(id);
    }

    public Tache save(Tache provenance) {
        return tacheRepository.saveAndFlush(provenance);
    }

    public boolean existById(Long id) {
        return tacheRepository.existsById(id);
    }

    public void deleteTache(Long id) {
        tacheRepository.deleteById(id);
    }

    public List<Tache> findByMat(Matiere matiere) {
        return tacheRepository.findTachesByMatiere(matiere);
    }
}