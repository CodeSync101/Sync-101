package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;

import java.util.List;

public interface ReclamationService {
    Reclamation ajouterReclamation(Reclamation reclamation, Long userId, Long matiereId);
    Reclamation modifierReclamation(Long id, Reclamation updatedReclamation);
    void supprimerReclamation(Long id);
    List<Reclamation> getAllReclamations();
    Reclamation changerStatutReclamation(Long id, String action);
    Reclamation getReclamationById(Long id);
    List<Reclamation> getReclamationsParPriorite(Priorite priorite);
    List<Reclamation> getReclamationsParStatut(Statut statut);
    List<Reclamation> getReclamationsNonTraiteesParAnciennete();
    List<Reclamation> getReclamationsParMatiere(Long matiereId);
    Reclamation traiterReclamation(Long id);
    Reclamation ajouterReclamationSimple(Reclamation reclamation, Long userId);
}
