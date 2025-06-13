package com.example.GestionRec_Not.controllers;

import com.example.GestionRec_Not.models.NotificationMessage;
import com.example.GestionRec_Not.services.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

@Controller
@CrossOrigin(origins = "http://localhost:4200")
public class WebSocketController {

    @Autowired
    private WebSocketService webSocketService;

    /**
     * Test d'envoi de notification via WebSocket
     * URL: /app/test-notification
     */
    @MessageMapping("/test-notification")
    @SendTo("/topic/notifications")
    public NotificationMessage testNotification(NotificationMessage message) {
        message.setDateHeure(LocalDateTime.now());
        
        if (message.getMessage() == null || message.getMessage().isEmpty()) {
            message.setMessage("Notification de test");
        }
        
        if (message.getType() == null || message.getType().isEmpty()) {
            message.setType("TEST");
        }
        
        return message;
    }
    
    /**
     * Envoie une notification globale
     * URL: /app/envoyer-notification
     */
    @MessageMapping("/envoyer-notification")
    public void envoyerNotification(NotificationMessage notification) {
        notification.setDateHeure(LocalDateTime.now());
        webSocketService.envoyerNotification(notification);
    }
} 