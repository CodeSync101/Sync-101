package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReclamationRepo extends JpaRepository<Reclamation, Long> {
    
    // Recherche par priorité
    List<Reclamation> findByPriorite(Priorite priorite);
    
    // Recherche par statut
    List<Reclamation> findByStatut(Statut statut);
    
    // Recherche par statut et tri par date de création (ascendant)
    List<Reclamation> findByStatutOrderByDateCreationAsc(Statut statut);
    
    // Recherche par statut et tri par date de création (descendant)
    List<Reclamation> findByStatutOrderByDateCreationDesc(Statut statut);
    
    // Recherche par priorité et statut
    List<Reclamation> findByPrioriteAndStatut(Priorite priorite, Statut statut);
    
    // Recherche par mot-clé dans le titre ou la description
    @Query("SELECT r FROM Reclamation r WHERE LOWER(r.titre) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Reclamation> findByKeyword(String keyword);
    
    // Recherche des réclamations créées entre deux dates
    List<Reclamation> findByDateCreationBetween(LocalDate dateDebut, LocalDate dateFin);
    
    // Compter le nombre de réclamations par priorité
    long countByPriorite(Priorite priorite);
    
    // Compter le nombre de réclamations par statut
    long countByStatut(Statut statut);
    
    // Recherche par matière
    List<Reclamation> findByMatiereId(Long matiereId);
}
