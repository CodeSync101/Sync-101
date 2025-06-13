package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Tache;
import java.util.List;

public interface TacheService {
    Tache ajouterTache(Tache tache, Long userId, Long matiereId);
    Tache modifierTache(Long id, Tache tache);
    void supprimerTache(Long id);
    List<Tache> getAllTaches();
    Tache getTacheById(Long id);
    List<Tache> getTachesByUser(Long userId);
    List<Tache> getTachesByMatiere(Long matiereId);
    List<Tache> getTachesByUserAndMatiere(Long userId, Long matiereId);
    List<Tache> getTachesTerminees(Boolean terminee);
    Tache marquerCommeTerminee(Long id, Boolean terminee);
} 