package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;

import java.util.List;

public interface ReclamationService {
    Reclamation ajouterReclamation(Reclamation reclamation, Long userId);
    Reclamation modifierReclamation(Long id, Reclamation updatedReclamation);
    void supprimerReclamation(Long id);
    List<Reclamation> getAllReclamations();
    Reclamation changerStatutReclamation(Long id, String action);
}
