package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.repository.ReclamationRepo;
import com.example.GestionRec_Not.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ReclamationServiceImp implements ReclamationService {

    @Autowired
    private ReclamationRepo reclamationRepo;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Reclamation ajouterReclamation(Reclamation reclamation, Long userId) {
        Userr user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        reclamation.setUser(user);
        reclamation.setDateCreation(LocalDate.now());
        return reclamationRepo.save(reclamation);
    }

    @Override
    public Reclamation modifierReclamation(Long id, Reclamation updatedReclamation) {
        Reclamation reclamation = reclamationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));

        reclamation.setTitre(updatedReclamation.getTitre());
        reclamation.setDescription(updatedReclamation.getDescription());
        reclamation.setStatut(updatedReclamation.getStatut());
        reclamation.setPriorite(updatedReclamation.getPriorite());

        return reclamationRepo.save(reclamation);
    }

    @Override
    public void supprimerReclamation(Long id) {
        reclamationRepo.deleteById(id);
    }

    @Override
    public List<Reclamation> getAllReclamations() {
        return reclamationRepo.findAll();
    }
}
