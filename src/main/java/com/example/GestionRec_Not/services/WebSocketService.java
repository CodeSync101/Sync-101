package com.example.GestionRec_Not.services;

import com.example.GestionRec_Not.models.NotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Envoie une notification à tous les utilisateurs connectés
     * @param notification La notification à envoyer
     */
    public void envoyerNotification(NotificationMessage notification) {
        // Envoyer à tous les abonnés du topic /topic/notifications
        messagingTemplate.convertAndSend("/topic/notifications", notification);
    }
    
    /**
     * Envoie une notification à un utilisateur spécifique
     * @param userId L'identifiant de l'utilisateur
     * @param notification La notification à envoyer
     */
    public void envoyerNotificationPrivee(Long userId, NotificationMessage notification) {
        // Envoyer à un canal spécifique à l'utilisateur
        messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
    }
    
    /**
     * Envoie une notification concernant une matière spécifique
     * @param matiereId L'identifiant de la matière
     * @param notification La notification à envoyer
     */
    public void envoyerNotificationMatiere(Long matiereId, NotificationMessage notification) {
        // Envoyer à tous ceux qui suivent les notifications pour cette matière
        messagingTemplate.convertAndSend("/topic/matiere/" + matiereId, notification);
    }
} 