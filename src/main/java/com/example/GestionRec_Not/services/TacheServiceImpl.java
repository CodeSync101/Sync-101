package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Matiere;
import com.example.GestionRec_Not.entities.Tache;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.repository.MatiereRepository;
import com.example.GestionRec_Not.repository.TacheRepository;
import com.example.GestionRec_Not.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TacheServiceImpl implements TacheService {

    @Autowired
    private TacheRepository tacheRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MatiereRepository matiereRepository;

    @Override
    public Tache ajouterTache(Tache tache, Long userId, Long matiereId) {
        // Récupérer l'utilisateur
        Userr user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Récupérer la matière
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        
        // Configurer la tâche
        tache.setUser(user);
        tache.setMatiere(matiere);
        tache.setDateCreation(LocalDateTime.now());
        
        return tacheRepository.save(tache);
    }

    @Override
    public Tache modifierTache(Long id, Tache updatedTache) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        
        tache.setTitre(updatedTache.getTitre());
        tache.setDescription(updatedTache.getDescription());
        tache.setDateEcheance(updatedTache.getDateEcheance());
        
        if (updatedTache.getTerminee() != null) {
            tache.setTerminee(updatedTache.getTerminee());
        }
        
        return tacheRepository.save(tache);
    }

    @Override
    public void supprimerTache(Long id) {
        tacheRepository.deleteById(id);
    }

    @Override
    public List<Tache> getAllTaches() {
        return tacheRepository.findAll();
    }

    @Override
    public Tache getTacheById(Long id) {
        return tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
    }

    @Override
    public List<Tache> getTachesByUser(Long userId) {
        return tacheRepository.findByUserId(userId);
    }

    @Override
    public List<Tache> getTachesByMatiere(Long matiereId) {
        return tacheRepository.findByMatiereId(matiereId);
    }

    @Override
    public List<Tache> getTachesByUserAndMatiere(Long userId, Long matiereId) {
        return tacheRepository.findByUserIdAndMatiereId(userId, matiereId);
    }

    @Override
    public List<Tache> getTachesTerminees(Boolean terminee) {
        return tacheRepository.findByTerminee(terminee);
    }

    @Override
    public Tache marquerCommeTerminee(Long id, Boolean terminee) {
        Tache tache = tacheRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        
        tache.setTerminee(terminee);
        return tacheRepository.save(tache);
    }
} 