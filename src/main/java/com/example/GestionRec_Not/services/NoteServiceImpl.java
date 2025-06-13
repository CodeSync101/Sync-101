package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Note;
import com.example.GestionRec_Not.entities.Role;
import com.example.GestionRec_Not.entities.Tache;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.repository.NoteRepository;
import com.example.GestionRec_Not.repository.TacheRepository;
import com.example.GestionRec_Not.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final TacheRepository tacheRepository;
    private final EmailService emailService;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, UserRepository userRepository, 
                          TacheRepository tacheRepository, EmailService emailService) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
        this.tacheRepository = tacheRepository;
        this.emailService = emailService;
    }

    @Override
    public Note ajouterNote(Note note, Long userId, Long tacheId) {
        // Récupérer l'utilisateur (professeur)
        Userr enseignant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        // Vérifier si l'utilisateur est bien un enseignant
        if (enseignant.getRole() != Role.ENSEIGNANT) {
            throw new RuntimeException("L'utilisateur n'est pas un enseignant");
        }
        
        // Récupérer la tâche
        Tache tache = tacheRepository.findById(tacheId)
                .orElseThrow(() -> new RuntimeException("Tâche non trouvée"));
        
        // Récupérer l'étudiant qui a cette tâche
        Userr etudiant = tache.getUser();
        
        // Configurer la note
        note.setUser(enseignant);
        note.setTache(tache);
        note.setDateCreation(LocalDateTime.now());
        
        // Sauvegarder la note
        Note savedNote = noteRepository.save(note);
        
        // Envoyer une notification par email à l'étudiant
        emailService.sendNoteNotification(
            etudiant.getEmail(),
            etudiant.getNom(),
            enseignant.getNom(),
            tache.getMatiere().getNomMatiere(),
            note.getValeur()
        );
        
        return savedNote;
    }

    @Override
    public Note modifierNote(Long id, Note updatedNote) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
        
        note.setValeur(updatedNote.getValeur());
        note.setCommentaire(updatedNote.getCommentaire());
        
        return noteRepository.save(note);
    }

    @Override
    public void supprimerNote(Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
        
        noteRepository.deleteById(id);
    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public Note getNoteById(Long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note non trouvée"));
    }

    @Override
    public List<Note> getNotesByUser(Long userId) {
        return noteRepository.findByUserId(userId);
    }

    @Override
    public List<Note> getNotesByTache(Long tacheId) {
        return noteRepository.findByTacheId(tacheId);
    }

    @Override
    public List<Note> getNotesByUserAndTache(Long userId, Long tacheId) {
        return noteRepository.findByUserIdAndTacheId(userId, tacheId);
    }
} 