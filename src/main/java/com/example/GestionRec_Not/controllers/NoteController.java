package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.entities.Note;
import com.example.GestionRec_Not.services.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    @Autowired
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping("/add/{userId}/{tacheId}")
    public ResponseEntity<Note> ajouterNote(@RequestBody Note note, 
                                           @PathVariable Long userId,
                                           @PathVariable Long tacheId) {
        Note newNote = noteService.ajouterNote(note, userId, tacheId);
        return new ResponseEntity<>(newNote, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> modifierNote(@PathVariable Long id, @RequestBody Note note) {
        Note updatedNote = noteService.modifierNote(id, note);
        return new ResponseEntity<>(updatedNote, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerNote(@PathVariable Long id) {
        noteService.supprimerNote(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes() {
        List<Note> notes = noteService.getAllNotes();
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Long id) {
        Note note = noteService.getNoteById(id);
        return new ResponseEntity<>(note, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Note>> getNotesByUser(@PathVariable Long userId) {
        List<Note> notes = noteService.getNotesByUser(userId);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/tache/{tacheId}")
    public ResponseEntity<List<Note>> getNotesByTache(@PathVariable Long tacheId) {
        List<Note> notes = noteService.getNotesByTache(tacheId);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }

    @GetMapping("/user/{userId}/tache/{tacheId}")
    public ResponseEntity<List<Note>> getNotesByUserAndTache(
            @PathVariable Long userId, 
            @PathVariable Long tacheId) {
        List<Note> notes = noteService.getNotesByUserAndTache(userId, tacheId);
        return new ResponseEntity<>(notes, HttpStatus.OK);
    }
} 