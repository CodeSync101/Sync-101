package com.example.GestionRec_Not;

import com.example.GestionRec_Not.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GestionRecNotApplication {

	private static final Logger logger = LoggerFactory.getLogger(GestionRecNotApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GestionRecNotApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner testEmailConfig(@Autowired EmailService emailService) {
		return args -> {
			logger.info("Test d'envoi d'email au démarrage de l'application...");
			try {
				emailService.sendReclamationNotification(
					"farah.saad505@gmail.com", // remplacez par votre email de test
					"Test Professeur",
					"Test Étudiant",
					"Test Matière",
					10.0f,
					"Test Réclamation",
					"Ceci est un test pour vérifier la configuration d'envoi d'emails."
				);
				logger.info("Email de test envoyé avec succès!");
			} catch (Exception e) {
				logger.error("Erreur lors de l'envoi de l'email de test", e);
			}
		};
	}
}
