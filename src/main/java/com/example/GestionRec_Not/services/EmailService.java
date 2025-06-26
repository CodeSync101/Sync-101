package com.example.GestionRec_Not.services;

public interface EmailService {
    void sendNoteNotification(String to, String studentName, String professorName, String subjectName, Double grade);
    void sendReclamationNotification(String to, String professorName, String studentName, String subjectName, Float grade, String reclamationTitle, String reclamationDescription);
    void sendReclamationTraiteeNotification(String to, String studentName, String subjectName, String reclamationTitle);
    void sendSimpleReclamationNotification(String to, String studentName, String reclamationTitle, String reclamationDescription);
} 