package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Matiere;
import com.example.GestionRec_Not.entities.Role;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.repository.MatiereRepository;
import com.example.GestionRec_Not.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatiereServiceImpl implements MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public Matiere ajouterMatiere(Matiere matiere) {
        return matiereRepository.save(matiere);
    }

    @Override
    public Matiere modifierMatiere(Long id, Matiere matiere) {
        Matiere existingMatiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        
        existingMatiere.setNomMatiere(matiere.getNomMatiere());
        existingMatiere.setNoteMatiere(matiere.getNoteMatiere());
        
        // Si un étudiant est défini dans la matière mise à jour, le prendre en compte
        if (matiere.getEtudiant() != null) {
            existingMatiere.setEtudiant(matiere.getEtudiant());
        }
        
        return matiereRepository.save(existingMatiere);
    }

    @Override
    public void supprimerMatiere(Long id) {
        matiereRepository.deleteById(id);
    }

    @Override
    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    @Override
    public Matiere getMatiereById(Long id) {
        return matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
    }

    @Override
    public Matiere getByNomMatiere(String nomMatiere) {
        return matiereRepository.findByNomMatiere(nomMatiere);
    }
    
    @Override
    public Matiere assignerEtudiantAMatiere(Long matiereId, Long etudiantId) {
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
                
        Userr etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
                
        // Vérifier que l'utilisateur est bien un étudiant
        if (etudiant.getRole() != Role.ETUDIANT) {
            throw new RuntimeException("L'utilisateur n'est pas un étudiant");
        }
        
        matiere.setEtudiant(etudiant);
        return matiereRepository.save(matiere);
    }
    
    @Override
    public List<Matiere> getMatieresByEtudiantId(Long etudiantId) {
        return matiereRepository.findByEtudiantId(etudiantId);
    }
} 