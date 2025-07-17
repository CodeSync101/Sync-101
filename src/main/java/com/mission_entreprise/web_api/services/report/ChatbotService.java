package com.mission_entreprise.web_api.services.report;

import com.mission_entreprise.web_api.repositories.report.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatbotService {

    @Autowired private BranchReportRepository branchRepository;
    @Autowired private CommitReportRepository commitRepository;
    @Autowired private IssueReportRepository issueRepository;
    @Autowired private PullRequestReportRepository pullRequestRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private UtilisateurRepository utilisateurRepository;
    @Autowired private OpenRouterService openRouterService;

    private static final Map<String, String> keywordMap = Map.ofEntries(
            Map.entry("commit", "commit"), Map.entry("commits", "commit"), Map.entry("commité", "commit"),
            Map.entry("ticket", "ticket"), Map.entry("tickets", "ticket"),
            Map.entry("branche", "branch"), Map.entry("branches", "branch"),
            Map.entry("pr", "pull request"), Map.entry("pull request", "pull request"),
            Map.entry("pull requests", "pull request"),
            Map.entry("issue", "issue"), Map.entry("issues", "issue"),
            Map.entry("utilisateur", "user"), Map.entry("utilisateurs", "user"),
            Map.entry("étudiant", "user"), Map.entry("étudiants", "user"),
            Map.entry("nombre", "count"), Map.entry("combien", "count"),
            Map.entry("statut", "status"), Map.entry("statuts", "status"),
            Map.entry("date", "date"),
            Map.entry("bonjour", "hello"), Map.entry("salut", "hello"),
            Map.entry("merci", "thanks"), Map.entry("remerciement", "thanks"),
            Map.entry("bilan", "summary"), Map.entry("statistiques", "summary"),
            Map.entry("total", "count")
    );

    private static final Pattern datePattern = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b");

    public String processPrompt(String userPrompt) {
        String normalizedPrompt = normalizePrompt(userPrompt.toLowerCase());
        String[] subPrompts = normalizedPrompt.split("\\bet\\b|,");
        StringBuilder combinedResponse = new StringBuilder();

        for (String subPrompt : subPrompts) {
            subPrompt = subPrompt.trim();
            if (subPrompt.isEmpty()) continue;

            String rawResponse = processRawPrompt(subPrompt);
            String enhancedPrompt = "Formulez une réponse conviviale et concise basée sur les informations suivantes : " + rawResponse;
            String response = openRouterService.generateResponse(enhancedPrompt);
            combinedResponse.append(response).append("\n\n");
        }

        return combinedResponse.toString().trim();
    }

    private String normalizePrompt(String prompt) {
        String[] words = prompt.split("\\W+");
        StringBuilder normalized = new StringBuilder();
        for (String w : words) {
            normalized.append(keywordMap.getOrDefault(w, w)).append(" ");
        }
        return normalized.toString().trim();
    }

    private String extractDate(String prompt) {
        Matcher matcher = datePattern.matcher(prompt);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String processRawPrompt(String prompt) {
        if (prompt.matches(".*\\b(hello|hi|thanks|salut|bonjour|merci)\\b.*")) {
            return generateGreetingResponse(prompt);
        }

        if (prompt.contains("commit") && prompt.contains("user")) {
            List<Object[]> results = commitRepository.countCommitsByEtudiant();
            return formatCommitResults(results);
        } else if (prompt.contains("commit") && prompt.contains("date")) {
            String date = extractDate(prompt);
            List<Object[]> results = commitRepository.countCommitsByDate();
            if (date != null && results.isEmpty()) return "Aucun commit trouvé pour la date " + date + ".";
            return formatCommitByDateResults(results);
        } else if (prompt.contains("ticket") && prompt.contains("status")) {
            List<Object[]> results = ticketRepository.countTicketsByStatus();
            return formatTicketResults(results);
        } else if (prompt.contains("branch") && prompt.contains("date")) {
            String date = extractDate(prompt);
            List<Object[]> results = branchRepository.countBranchesByDate();
            if (date != null && results.isEmpty()) return "Aucune branche trouvée pour la date " + date + ".";
            return formatBranchResults(results);
        } else if (prompt.contains("branch") && prompt.contains("user")) {
            List<Object[]> results = branchRepository.findBranchesWithUserDetails();
            return formatBranchUserResults(results);
        } else if (prompt.contains("pull request")) {
            long count = pullRequestRepository.count();
            return "Le nombre total de pull requests est : " + count;
        } else if (prompt.contains("issue")) {
            long count = issueRepository.count();
            return "Le nombre total d’issues est : " + count;
        } else if (prompt.contains("user") && prompt.contains("count")) {
            long count = utilisateurRepository.count();
            return "Le nombre total d'utilisateurs enregistrés est : " + count;
        } else if (prompt.contains("summary")) {
            long commits = commitRepository.count();
            long branches = branchRepository.count();
            long tickets = ticketRepository.count();
            long prs = pullRequestRepository.count();
            long issues = issueRepository.count();

            return String.format(
                    "Voici les statistiques globales :\n" +
                            "- Commits : %d\n" +
                            "- Branches : %d\n" +
                            "- Tickets : %d\n" +
                            "- Pull Requests : %d\n" +
                            "- Issues : %d",
                    commits, branches, tickets, prs, issues
            );
        } else {
            return "Désolé, je ne comprends pas votre demande. Essayez par exemple « commits par utilisateur », « tickets par statut », ou « branches par date ».";
        }
    }

    private String generateGreetingResponse(String prompt) {
        if (prompt.contains("hello") || prompt.contains("hi")) {
            if (prompt.contains("thank") || prompt.contains("thanks")) {
                return "You're welcome! Feel free to ask more questions.";
            }
            return "Hello! How can I assist you today?";
        } else {
            if (prompt.contains("merci") || prompt.contains("thanks")) {
                return "Avec plaisir ! N'hésitez pas si vous avez d'autres questions.";
            }
            return "Bonjour ! Comment puis-je vous aider aujourd'hui ?";
        }
    }

    private String formatCommitResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Voici le nombre de commits par utilisateur :\n");
        for (Object[] row : results) {
            response.append(String.format("- %s %s : %d commits\n", row[0], row[1], row[2]));
        }
        return response.toString();
    }

    private String formatCommitByDateResults(List<Object[]> results) {
        StringBuilder response = new StringBuilder("Voici le nombre de commits par date :\n");
        for (Object[] row : results) {
            response.append(String.format("- Date %s : %d commits\n", row[0], row[1]));
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
