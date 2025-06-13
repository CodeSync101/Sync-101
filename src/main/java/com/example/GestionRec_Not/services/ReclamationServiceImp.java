package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Matiere;
import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.entities.Note;
import com.example.GestionRec_Not.entities.Tache;
import com.example.GestionRec_Not.entities.Role;
import com.example.GestionRec_Not.repository.ReclamationRepo;
import com.example.GestionRec_Not.repository.UserRepository;
import com.example.GestionRec_Not.repository.MatiereRepository;
import com.example.GestionRec_Not.repository.NoteRepository;
import com.example.GestionRec_Not.repository.TacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReclamationServiceImp implements ReclamationService {

    private static final Logger logger = LoggerFactory.getLogger(ReclamationServiceImp.class);
    
    @Autowired
    private ReclamationRepo reclamationRepo;

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private MatiereRepository matiereRepository;
    
    @Autowired
    private PrioriteService prioriteService;
    
    @Autowired
    @Lazy
    private NotificationService notificationService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private TacheRepository tacheRepository;

    @Override
    public Reclamation ajouterReclamation(Reclamation reclamation, Long userId, Long matiereId) {
        Userr etudiant = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        
        reclamation.setUser(etudiant);
        reclamation.setMatiere(matiere);
        reclamation.setDateCreation(LocalDate.now());
        
        // Déterminer automatiquement la priorité
        Priorite prioriteCalculee = prioriteService.determinerPriorite(
            reclamation.getTitre(), 
            reclamation.getDescription()
        );
        reclamation.setPriorite(prioriteCalculee);
        
        // Définir le statut par défaut
        reclamation.setStatut(Statut.EN_ATTENTE);
        
        // Sauvegarder la réclamation
        Reclamation savedReclamation = reclamationRepo.save(reclamation);
        
        // Envoyer une notification adaptée à la priorité
        notificationService.envoyerNotificationCreation(savedReclamation);
        
        // Envoyer un email au professeur qui a donné la note
        try {
            logger.info("Recherche du professeur associé à la note pour la matière: {}, étudiant: {}", matiereId, userId);
            
            // Approche simplifiée : envoyer à tous les enseignants pour s'assurer que le bon professeur reçoit la notification
            List<Userr> enseignants = userRepository.findByRole(Role.ENSEIGNANT);
            logger.info("Nombre d'enseignants trouvés: {}", enseignants.size());
            
            if (!enseignants.isEmpty()) {
                for (Userr professeur : enseignants) {
                    if (professeur.getEmail() != null && !professeur.getEmail().isEmpty()) {
                        logger.info("Envoi d'email au professeur: {} ({})", professeur.getNom(), professeur.getEmail());
                        emailService.sendReclamationNotification(
                            professeur.getEmail(),
                            professeur.getNom(),
                            etudiant.getNom(),
                            matiere.getNomMatiere(),
                            matiere.getNoteMatiere(),
                            reclamation.getTitre(),
                            reclamation.getDescription()
                        );
                    } else {
                        logger.warn("Le professeur {} n'a pas d'email défini", professeur.getNom());
                    }
                }
            } else {
                logger.warn("Aucun enseignant trouvé dans le système. Impossible d'envoyer l'email.");
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email au professeur", e);
        }
        
        return savedReclamation;
    }

    @Override
    public Reclamation modifierReclamation(Long id, Reclamation updatedReclamation) {
        Reclamation reclamation = reclamationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));

        reclamation.setTitre(updatedReclamation.getTitre());
        reclamation.setDescription(updatedReclamation.getDescription());
        
        // Si la matière est fournie, la mettre à jour
        if (updatedReclamation.getMatiere() != null && updatedReclamation.getMatiere().getId() != null) {
            Matiere matiere = matiereRepository.findById(updatedReclamation.getMatiere().getId())
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
            reclamation.setMatiere(matiere);
        }
        
        // Recalculer la priorité basée sur le nouveau titre et description
        Priorite prioriteCalculee = prioriteService.determinerPriorite(
            updatedReclamation.getTitre(), 
            updatedReclamation.getDescription()
        );
        reclamation.setPriorite(prioriteCalculee);
        
        // Ne pas permettre à l'utilisateur de définir manuellement la priorité
        // mais garder le statut que l'admin peut changer
        reclamation.setStatut(updatedReclamation.getStatut());

        return reclamationRepo.save(reclamation);
    }

    @Override
    public void supprimerReclamation(Long id) {
        reclamationRepo.deleteById(id);
    }

    @Override
    public List<Reclamation> getAllReclamations() {
        return reclamationRepo.findAll();
    }
    
    @Override
    public Reclamation changerStatutReclamation(Long id, String action) {
        Reclamation reclamation = reclamationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));
        
        Statut ancienStatut = reclamation.getStatut();
        
        switch (action.toUpperCase()) {
            case "ACCEPTER":
                reclamation.setStatut(Statut.TRAITEE);
                break;
            case "REFUSER":
                reclamation.setStatut(Statut.REFUSEE);
                break;
            default:
                reclamation.setStatut(Statut.EN_ATTENTE);
                break;
        }
        
        // Envoyer une notification si le statut a changé
        if (ancienStatut != reclamation.getStatut()) {
            notificationService.envoyerNotificationChangementStatut(reclamation, ancienStatut);
        }
        
        return reclamationRepo.save(reclamation);
    }
    
    @Override
    public Reclamation getReclamationById(Long id) {
        return reclamationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));
    }
    
    @Override
    public List<Reclamation> getReclamationsParPriorite(Priorite priorite) {
        return reclamationRepo.findByPriorite(priorite);
    }
    
    @Override
    public List<Reclamation> getReclamationsParStatut(Statut statut) {
        return reclamationRepo.findByStatut(statut);
    }
    
    @Override
    public List<Reclamation> getReclamationsNonTraiteesParAnciennete() {
        return reclamationRepo.findByStatutOrderByDateCreationAsc(Statut.EN_ATTENTE);
    }
    
    @Override
    public List<Reclamation> getReclamationsParMatiere(Long matiereId) {
        return reclamationRepo.findByMatiereId(matiereId);
    }

    @Override
    public Reclamation traiterReclamation(Long id) {
        Reclamation reclamation = reclamationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Réclamation non trouvée"));
        
        // Marquer la réclamation comme traitée
        reclamation.setTraitee(true);
        reclamation.setStatut(Statut.TRAITEE);
        
        // Sauvegarder les modifications
        Reclamation savedReclamation = reclamationRepo.save(reclamation);
        
        // Envoyer une notification par email à l'étudiant
        try {
            Userr etudiant = reclamation.getUser();
            if (etudiant != null && etudiant.getEmail() != null) {
                emailService.sendReclamationTraiteeNotification(
                    etudiant.getEmail(),
                    etudiant.getNom(),
                    reclamation.getMatiere().getNomMatiere(),
                    reclamation.getTitre()
                );
            }
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification de traitement", e);
        }
        
        return savedReclamation;
    }
}
