package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Note;
import java.util.List;

public interface NoteService {
    Note ajouterNote(Note note, Long userId, Long tacheId);
    Note modifierNote(Long id, Note note);
    void supprimerNote(Long id);
    List<Note> getAllNotes();
    Note getNoteById(Long id);
    List<Note> getNotesByUser(Long userId);
    List<Note> getNotesByTache(Long tacheId);
    List<Note> getNotesByUserAndTache(Long userId, Long tacheId);
} 