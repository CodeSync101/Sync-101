package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Matiere;
import java.util.List;

public interface MatiereService {
    Matiere ajouterMatiere(Matiere matiere);
    Matiere modifierMatiere(Long id, Matiere matiere);
    void supprimerMatiere(Long id);
    List<Matiere> getAllMatieres();
    Matiere getMatiereById(Long id);
    Matiere getByNomMatiere(String nomMatiere);
    Matiere assignerEtudiantAMatiere(Long matiereId, Long etudiantId);
    List<Matiere> getMatieresByEtudiantId(Long etudiantId);
} 