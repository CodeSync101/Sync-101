package feten.tn.projetmission.Repositories;

import feten.tn.projetmission.Entities.Matiere;
import feten.tn.projetmission.Entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findTachesByMatiere(Matiere matiere);

}