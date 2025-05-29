package tn.missionentreprise.rapportservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.missionentreprise.rapportservice.repositories.BranchRepository;
import tn.missionentreprise.rapportservice.repositories.CommitRepository;
import tn.missionentreprise.rapportservice.repositories.IssueRepository;
import tn.missionentreprise.rapportservice.repositories.PullRequestRepository;
import tn.missionentreprise.rapportservice.repositories.TicketRepository;

import java.util.List;

@Service
public class ChatbotService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private OpenRouterService openRouterService;

    public String processPrompt(String userPrompt) {
        String rawResponse = processRawPrompt(userPrompt.toLowerCase());
        // Utiliser OpenRouter pour reformuler la réponse en une version conviviale
        String enhancedPrompt = "Formulez une réponse conviviale et concise basée sur les informations suivantes : " + rawResponse;
        String finalResponse = openRouterService.generateResponse(enhancedPrompt);
        System.out.println("Réponse finale du chatbot :\n" + finalResponse);
        return finalResponse;
    }

    private String processRawPrompt(String prompt) {
        // Gestion des salutations et expressions courantes
        if (prompt.matches(".*\\b(bonjour|salut|c\\'?est quoi|ça va|merci|thank you|thanks|hello|hi)\\b.*")) {
            return generateGreetingResponse(prompt);
        }

        if (prompt.contains("commits par utilisateur") || prompt.contains("nombre de commits")) {
            List<Object[]> results = commitRepository.countCommitsByEtudiant();
            System.out.println("Résultats des commits par utilisateur :");
            for (Object[] row : results) {
                System.out.println(row[0] + " " + row[1] + " : " + row[2] + " commits");
            }
            return formatCommitResults(results);
        } else if (prompt.contains("tickets par statut") || prompt.contains("statut des tickets")) {
            List<Object[]> results = ticketRepository.countTicketsByStatus();
            System.out.println("Résultats des tickets par statut :");
            for (Object[] row : results) {
                System.out.println("Statut " + row[0] + " : " + row[1] + " tickets");
            }
            return formatTicketResults(results);
        } else if (prompt.contains("branches par date") || prompt.contains("nombre de branches")) {
            List<Object[]> results = branchRepository.countBranchesByDate();
            System.out.println("Résultats des branches par date :");
            for (Object[] row : results) {
                System.out.println("Date " + row[0] + " : " + row[1] + " branches");
            }
            return formatBranchResults(results);
        } else if (prompt.contains("branches avec utilisateurs")) {
            List<Object[]> results = branchRepository.findBranchesWithUserDetails();
            System.out.println("Résultats des branches avec utilisateurs :");
            for (Object[] row : results) {
                System.out.println("Branche " + row[0] + ", par " + row[1] + " " + row[2] + ", commit le " + row[3]);
            }
            return formatBranchUserResults(results);
        } else {
            return "Désolé, je ne comprends pas votre demande. Essayez quelque chose comme « commits par utilisateur » ou « tickets par statut ».";
        }
    }

    private String generateGreetingResponse(String prompt) {
        prompt = prompt.toLowerCase();
        if (prompt.contains("bonjour") || prompt.contains("salut") || prompt.contains("hello") || prompt.contains("hi")) {
            return "Bonjour ! Comment puis-je vous aider aujourd'hui ?";
        } else if (prompt.contains("ça va") || prompt.contains("comment ça va")) {
            return "Je vais bien, merci ! Et vous ?";
        } else if (prompt.contains("merci") || prompt.contains("thank you") || prompt.contains("thanks")) {
            return "Avec plaisir ! N'hésitez pas si vous avez d'autres questions.";
        } else {
            return "Bonjour ! Que puis-je faire pour vous ?";
        }
    }

    private String formatCommitResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Voici le nombre de commits par utilisateur :\n");
        for (Object[] row : results) {
            response.append(String.format("- %s %s : %d commits\n", row[0], row[1], row[2]));
        }
        return response.toString();
    }

    private String formatTicketResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Voici le nombre de tickets par statut :\n");
        for (Object[] row : results) {
            response.append(String.format("- Statut %s : %d tickets\n", row[0], row[1]));
        }
        return response.toString();
    }

    private String formatBranchResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Voici le nombre de branches par date :\n");
        for (Object[] row : results) {
            response.append(String.format("- Date %s : %d branches\n", row[0], row[1]));
        }
        return response.toString();
    }

    private String formatBranchUserResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Détails des branches avec utilisateurs :\n");
        for (Object[] row : results) {
            response.append(String.format("- Branche %s, par %s %s, commit le %s\n", row[0], row[1], row[2], row[3]));
        }
        return response.toString();
    }
}
