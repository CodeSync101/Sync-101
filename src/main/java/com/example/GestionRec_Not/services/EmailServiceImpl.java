package com.example.GestionRec_Not.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String from;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNoteNotification(String to, String studentName, String professorName, String subjectName, Double grade) {
        logger.info("Préparation email de notification de note pour: {}", to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Nouvelle note pour " + subjectName);
        message.setText("Bonjour " + studentName + ",\n\n" +
                "Votre professeur " + professorName + " a attribué une note de " + grade + " pour la matière " + subjectName + ".\n\n" +
                "Cordialement,\n" +
                "L'équipe Sync-101");
        
        try {
            logger.info("Envoi email de notification de note à: {}", to);
            mailSender.send(message);
            logger.info("Email de notification de note envoyé avec succès à: {}", to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de notification de note à: {}", to, e);
        }
    }
    
    @Override
    public void sendReclamationNotification(String to, String professorName, String studentName, String subjectName, Float grade, String reclamationTitle, String reclamationDescription) {
        logger.info("Préparation email de réclamation pour professeur: {} à l'adresse: {}", professorName, to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Réclamation concernant la note de " + subjectName);
        message.setText("Bonjour " + professorName + ",\n\n" +
                "L'étudiant " + studentName + " a fait une réclamation concernant la note de " + grade + " pour la matière " + subjectName + ".\n\n" +
                "Titre de la réclamation: " + reclamationTitle + "\n" +
                "Description: " + reclamationDescription + "\n\n" +
                "Merci de traiter cette réclamation dans les meilleurs délais.\n\n" +
                "Cordialement,\n" +
                "L'équipe Sync-101");
        
        try {
            logger.info("Envoi email de réclamation à: {} ({})", professorName, to);
            mailSender.send(message);
            logger.info("Email de réclamation envoyé avec succès à: {} ({})", professorName, to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de réclamation à: {} ({})", professorName, to, e);
        }
    }

    @Override
    public void sendReclamationTraiteeNotification(String to, String studentName, String subjectName, String reclamationTitle) {
        logger.info("Préparation email de notification de traitement pour étudiant: {} à l'adresse: {}", studentName, to);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Votre réclamation a été traitée - " + subjectName);
        message.setText("Bonjour " + studentName + ",\n\n" +
                "Nous avons le plaisir de vous informer que votre réclamation concernant la matière " + subjectName + " a été traitée.\n\n" +
                "Titre de la réclamation: " + reclamationTitle + "\n\n" +
                "Vous pouvez consulter le statut de votre réclamation dans votre espace étudiant.\n\n" +
                "Cordialement,\n" +
                "L'équipe Sync-101");
        
        try {
            logger.info("Envoi email de notification de traitement à: {} ({})", studentName, to);
            mailSender.send(message);
            logger.info("Email de notification de traitement envoyé avec succès à: {} ({})", studentName, to);
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de l'email de notification de traitement à: {} ({})", studentName, to, e);
        }
    }

    @Override
    public void sendSimpleReclamationNotification(String to, String studentName, String reclamationTitle, String reclamationDescription) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject("Réclamation envoyée");
        message.setText("Bonjour " + studentName + ",\n\n" +
                "Votre réclamation a bien été envoyée.\n\n" +
                "Titre : " + reclamationTitle + "\n" +
                "Description : " + reclamationDescription + "\n\n" +
                "Nous vous répondrons dans les plus brefs délais.\n\n" +
                "Cordialement,\nL'équipe Sync-101");
        mailSender.send(message);
    }
} 