package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Tache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByUserId(Long userId);
    List<Tache> findByMatiereId(Long matiereId);
    List<Tache> findByUserIdAndMatiereId(Long userId, Long matiereId);
    List<Tache> findByTerminee(Boolean terminee);
} 