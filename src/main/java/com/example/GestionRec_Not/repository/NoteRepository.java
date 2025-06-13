package com.example.GestionRec_Not.repository;

import com.example.GestionRec_Not.entities.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByUserId(Long userId);
    List<Note> findByTacheId(Long tacheId);
    List<Note> findByUserIdAndTacheId(Long userId, Long tacheId);
} 