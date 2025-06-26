package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.entities.Priorite;
import com.example.GestionRec_Not.entities.Reclamation;
import com.example.GestionRec_Not.entities.Statut;
import com.example.GestionRec_Not.entities.Userr;
import com.example.GestionRec_Not.models.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service gérant les notifications pour les réclamations
 * avec un comportement adapté en fonction de la priorité
 */
@Service
public class NotificationService {

    @Autowired
    @Lazy
    private ReclamationService reclamationService;
    
    @Autowired
    private WebSocketService webSocketService;

    /**
     * Envoie une notification immédiate lors de la création d'une réclamation
     * Le message est adapté en fonction de la priorité
     */
    public void envoyerNotificationCreation(Reclamation reclamation) {
        Priorite priorite = reclamation.getPriorite();
        Userr destinataire = reclamation.getUser();
        
        String message;
        
        switch (priorite) {
            case HIGH:
                message = "URGENT: Votre réclamation #" + reclamation.getId() + 
                          " a été enregistrée avec une priorité HAUTE. Un administrateur va la traiter en urgence.";
                break;
            case MEDIUM:
                message = "Votre réclamation #" + reclamation.getId() + 
                          " a été enregistrée avec une priorité MOYENNE. Un administrateur va la traiter prochainement.";
                break;
            case LOW:
                message = "Votre réclamation #" + reclamation.getId() + 
                          " a été enregistrée avec une priorité BASSE. Un administrateur la traitera dès que possible.";
                break;
            default:
                message = "Votre réclamation #" + reclamation.getId() + " a été enregistrée.";
                break;
        }
        
        // Créer et envoyer une notification WebSocket
        NotificationMessage notification = new NotificationMessage();
        notification.setReclamationId(reclamation.getId());
        notification.setMessage(message);
        notification.setNomUtilisateur(destinataire.getNom());
        notification.setNomMatiere(reclamation.getMatiere().getLibelle());
        notification.setNoteMatiere(reclamation.getMatiere().getNoteMatiere());
        notification.setPriorite(priorite);
        notification.setDateHeure(LocalDateTime.now());
        notification.setType("CREATION");
        
        // Envoyer à tous les administrateurs
        webSocketService.envoyerNotification(notification);
        
        // Envoyer aussi une notification personnelle à l'utilisateur
        webSocketService.envoyerNotificationPrivee(destinataire.getId(), notification);
        
        // Notification console (pourrait être remplacée par un logger)
        System.out.println("Notification à " + destinataire.getNom() + ": " + message);
    }
    
    /**
     * Envoie une notification lors du changement de statut d'une réclamation
     */
    public void envoyerNotificationChangementStatut(Reclamation reclamation, Statut ancienStatut) {
        Userr destinataire = reclamation.getUser();
        
        String message = "Le statut de votre réclamation #" + reclamation.getId() + 
                         " concernant " + reclamation.getMatiere().getLibelle() + 
                         " a été changé de " + ancienStatut + " à " + reclamation.getStatut();
        
        NotificationMessage notification = new NotificationMessage();
        notification.setReclamationId(reclamation.getId());
        notification.setMessage(message);
        notification.setNomUtilisateur(destinataire.getNom());
        notification.setNomMatiere(reclamation.getMatiere().getLibelle());
        notification.setNoteMatiere(reclamation.getMatiere().getNoteMatiere());
        notification.setPriorite(reclamation.getPriorite());
        notification.setDateHeure(LocalDateTime.now());
        notification.setType("STATUT_CHANGE");
        
        // Envoyer à l'utilisateur concerné
        webSocketService.envoyerNotificationPrivee(destinataire.getId(), notification);
        
        // Notification console
        System.out.println("Notification à " + destinataire.getNom() + ": " + message);
    }
    
    /**
     * Envoie des rappels pour les réclamations non traitées
     * La fréquence dépend de la priorité
     */
    public void envoyerRappelsReclamationsNonTraitees() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        LocalDate aujourdhui = LocalDate.now();
        
        // Filtrer les réclamations non traitées
        List<Reclamation> reclamationsNonTraitees = reclamations.stream()
                .filter(r -> r.getStatut() == Statut.EN_ATTENTE)
                .collect(Collectors.toList());
        
        for (Reclamation reclamation : reclamationsNonTraitees) {
            LocalDate dateCreation = reclamation.getDateCreation();
            long joursEcoules = ChronoUnit.DAYS.between(dateCreation, aujourdhui);
            
            boolean doitEnvoyerRappel = false;
            String niveauUrgence = "";
            
            switch (reclamation.getPriorite()) {
                case HIGH:
                    // Pour les priorités HAUTES, envoyer un rappel tous les jours
                    doitEnvoyerRappel = joursEcoules > 0 && joursEcoules % 1 == 0;
                    niveauUrgence = "TRÈS URGENT";
                    break;
                case MEDIUM:
                    // Pour les priorités MOYENNES, envoyer un rappel tous les 3 jours
                    doitEnvoyerRappel = joursEcoules > 0 && joursEcoules % 3 == 0;
                    niveauUrgence = "RAPPEL";
                    break;
                case LOW:
                    // Pour les priorités BASSES, envoyer un rappel tous les 7 jours
                    doitEnvoyerRappel = joursEcoules > 0 && joursEcoules % 7 == 0;
                    niveauUrgence = "INFORMATION";
                    break;
            }
            
            if (doitEnvoyerRappel) {
                String message = niveauUrgence + ": La réclamation #" + reclamation.getId() + 
                               " est en attente depuis " + joursEcoules + " jours. Priorité: " + 
                               reclamation.getPriorite();
                
                NotificationMessage notification = new NotificationMessage();
                notification.setReclamationId(reclamation.getId());
                notification.setMessage(message);
                notification.setNomUtilisateur(reclamation.getUser().getNom());
                notification.setNomMatiere(reclamation.getMatiere().getLibelle());
                notification.setNoteMatiere(reclamation.getMatiere().getNoteMatiere());
                notification.setPriorite(reclamation.getPriorite());
                notification.setDateHeure(LocalDateTime.now());
                notification.setType("RAPPEL");
                
                // Envoyer à tous les administrateurs
                webSocketService.envoyerNotification(notification);
                
                // Notification console
                System.out.println("Rappel aux administrateurs: " + message);
            }
        }
    }
    
    /**
     * Définit le temps de réponse cible en fonction de la priorité
     * @param priorite La priorité de la réclamation
     * @return Le nombre d'heures cible pour traiter la réclamation
     */
    public int getTempsReponseCibleEnHeures(Priorite priorite) {
        switch (priorite) {
            case HIGH:
                return 24; // 24 heures pour les priorités hautes
            case MEDIUM:
                return 72; // 3 jours pour les priorités moyennes
            case LOW:
                return 168; // 7 jours pour les priorités basses
            default:
                return 72; 
        }
    }
    
    /**
     * Calcule le niveau d'alerte pour une réclamation en fonction de son temps d'attente
     * @param reclamation La réclamation à évaluer
     * @return Un nombre entre 0 et 3 indiquant le niveau d'alerte
     *         0: Normal, 1: Attention, 2: Préoccupant, 3: Critique
     */
    public int getNiveauAlerte(Reclamation reclamation) {
        if (reclamation.getStatut() != Statut.EN_ATTENTE) {
            return 0; // Pas d'alerte pour les réclamations déjà traitées
        }
        
        LocalDate dateCreation = reclamation.getDateCreation();
        LocalDate aujourdhui = LocalDate.now();
        long heuresEcoulees = ChronoUnit.HOURS.between(
            dateCreation.atStartOfDay(), 
            aujourdhui.atStartOfDay()
        );
        
        int tempsCible = getTempsReponseCibleEnHeures(reclamation.getPriorite());
        double ratio = (double) heuresEcoulees / tempsCible;
        
        if (ratio >= 1.5) return 3; // Critique: dépassement de 50% ou plus
        if (ratio >= 1.0) return 2; // Préoccupant: dépassement du temps cible
        if (ratio >= 0.7) return 1; // Attention: approche du temps cible
        return 0; // Normal
    }
} 