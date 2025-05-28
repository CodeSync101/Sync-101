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
        return openRouterService.generateResponse(enhancedPrompt);
    }

    private String processRawPrompt(String prompt) {
        if (prompt.contains("commits par utilisateur") || prompt.contains("nombre de commits")) {
            List<Object[]> results = commitRepository.countCommitsByEtudiant(null, null, null);
            return formatCommitResults(results);
        } else if (prompt.contains("tickets par statut") || prompt.contains("statut des tickets")) {
            List<Object[]> results = ticketRepository.countTicketsByStatus(null, null, null);
            return formatTicketResults(results);
        } else if (prompt.contains("branches par date") || prompt.contains("nombre de branches")) {
            List<Object[]> results = branchRepository.countBranchesByDate(null, null, null);
            return formatBranchResults(results);
        } else if (prompt.contains("branches avec utilisateurs")) {
            List<Object[]> results = branchRepository.findBranchesWithUserDetails(null, null, null);
            return formatBranchUserResults(results);
        } else {
            return "Désolé, je ne comprends pas votre demande. Essayez de demander quelque chose comme 'nombre de commits par utilisateur' ou 'tickets par statut'.";
        }
    }

    private String formatCommitResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Commits par utilisateur :\n");
        for (Object[] row : results) {
            response.append(String.format("%s %s : %d commits\n", row[0], row[1], row[2]));
        }
        return response.length() > "Commits par utilisateur :\n".length() ?
                response.toString() : "Aucun commit trouvé.";
    }

    private String formatTicketResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Tickets par statut :\n");
        for (Object[] row : results) {
            response.append(String.format("Statut %s : %d tickets\n", row[0], row[1]));
        }
        return response.length() > "Tickets par statut :\n".length() ?
                response.toString() : "Aucun ticket trouvé.";
    }

    private String formatBranchResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Branches par date :\n");
        for (Object[] row : results) {
            response.append(String.format("Date %s : %d branches\n", row[0], row[1]));
        }
        return response.length() > "Branches par date :\n".length() ?
                response.toString() : "Aucune branche trouvée.";
    }

    private String formatBranchUserResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Détails des branches :\n");
        for (Object[] row : results) {
            response.append(String.format("Branche %s, par %s %s, commit le %s\n", row[0], row[1], row[2], row[3]));
        }
        return response.length() > "Détails des branches :\n".length() ?
                response.toString() : "Aucune branche trouvée.";
    }
}