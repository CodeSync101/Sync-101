package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {
    // Méthode pour trouver une matière par son nom
    Matiere findByNomMatiere(String nomMatiere);
    
    // Méthode pour trouver les matières associées à un étudiant
    List<Matiere> findByEtudiantId(Long etudiantId);
} 