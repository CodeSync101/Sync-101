package com.mission_entreprise.web_api.services.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mission_entreprise.web_api.entities.report.*;
import com.mission_entreprise.web_api.repositories.report.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

@Service
public class GitHubDataService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubDataService.class);

    @Value("${github.token}")
    private String githubToken;

    @Value("${github.repo.owner}")
    private String repoOwner;

    @Value("${github.repo.name}")
    private String repoName;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final CommitReportRepository commitRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final BranchReportRepository branchRepository;
    private final IssueReportRepository issueRepository;
    private final PullRequestReportRepository pullRequestRepository;

    public GitHubDataService(CommitReportRepository commitRepository,
                             UtilisateurRepository utilisateurRepository,
                             BranchReportRepository branchRepository,
                             IssueReportRepository issueRepository,
                             PullRequestReportRepository pullRequestRepository) {
        this.commitRepository = commitRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.branchRepository = branchRepository;
        this.issueRepository = issueRepository;
        this.pullRequestRepository = pullRequestRepository;
    }

    // Exécute toutes les 30 minutes
    @Scheduled(fixedRate = 1800000)
    public void syncGitHubData() {
        logger.info("Début de la synchronisation des données GitHub...");

        try {
            syncCommits();
            syncBranches();
            syncIssues();
            syncPullRequests();
            logger.info("Synchronisation des données GitHub terminée avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la synchronisation des données GitHub", e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        return headers;
    }

    private void syncCommits() {
        try {
            logger.info("Synchronisation des commits...");
            String url = String.format("https://api.github.com/repos/%s/%s/commits", repoOwner, repoName);

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode commits = objectMapper.readTree(response.getBody());

                for (JsonNode commitNode : commits) {
                    String sha = commitNode.get("sha").asText();

                    // Vérifier si le commit existe déjà
                    if (commitRepository.findAll().stream().noneMatch(c -> c.getSha().equals(sha))) {
                        CommitReport commit = new CommitReport();
                        commit.setSha(sha);
                        commit.setMessage(commitNode.get("commit").get("message").asText());

                        // Gestion de la date
                        String dateStr = commitNode.get("commit").get("author").get("date").asText();
                        Date commitDate = Date.from(LocalDateTime.parse(dateStr.replace("Z", ""),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")).atZone(java.time.ZoneId.systemDefault()).toInstant());
                        commit.setDateCommit(commitDate);

                        // Gestion de l'utilisateur
                        JsonNode authorNode = commitNode.get("commit").get("author");
                        String authorName = authorNode.get("name").asText();
                        String authorEmail = authorNode.get("email").asText();

                        Utilisateur utilisateur = getOrCreateUser(authorName, authorEmail);
                        commit.setUtilisateur(utilisateur);

                        // Stats des fichiers modifiés (nécessite un appel API supplémentaire)
                        commit.setFichiersChanges(getCommitStats(sha));

                        commitRepository.save(commit);
                        logger.debug("Commit sauvegardé: {}", sha);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la synchronisation des commits", e);
        }
    }

    private int getCommitStats(String sha) {
        try {
            String url = String.format("https://api.github.com/repos/%s/%s/commits/%s", repoOwner, repoName, sha);
            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode commitDetail = objectMapper.readTree(response.getBody());
                JsonNode files = commitDetail.get("files");
                return files != null ? files.size() : 0;
            }
        } catch (Exception e) {
            logger.warn("Impossible de récupérer les stats pour le commit: {}", sha);
        }
        return 0;
    }

    private void syncBranches() {
        try {
            logger.info("Synchronisation des branches...");
            String url = String.format("https://api.github.com/repos/%s/%s/branches", repoOwner, repoName);

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode branches = objectMapper.readTree(response.getBody());

                for (JsonNode branchNode : branches) {
                    String branchName = branchNode.get("name").asText();

                    // Vérifier si la branche existe déjà
                    if (branchRepository.findAll().stream().noneMatch(b -> b.getName().equals(branchName))) {
                        BranchReport branch = new BranchReport();
                        branch.setName(branchName);
                        branch.setProtected(branchNode.get("protected").asBoolean());

                        // Récupérer le commit associé
                        String commitSha = branchNode.get("commit").get("sha").asText();
                        Optional<CommitReport> commit = commitRepository.findAll().stream()
                                .filter(c -> c.getSha().equals(commitSha))
                                .findFirst();

                        if (commit.isPresent()) {
                            branch.setCommitId(commit.get().getId());
                        }

                        branchRepository.save(branch);
                        logger.debug("Branche sauvegardée: {}", branchName);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la synchronisation des branches", e);
        }
    }

    private void syncIssues() {
        try {
            logger.info("Synchronisation des issues...");
            String url = String.format("https://api.github.com/repos/%s/%s/issues?state=all", repoOwner, repoName);

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode issues = objectMapper.readTree(response.getBody());

                for (JsonNode issueNode : issues) {
                    // Ignorer les pull requests (elles ont un champ "pull_request")
                    if (issueNode.has("pull_request")) {
                        continue;
                    }

                    Long issueNumber = issueNode.get("number").asLong();

                    // Vérifier si l'issue existe déjà (en utilisant l'ID GitHub)
                    if (issueRepository.findAll().stream().noneMatch(i -> i.getId().equals(issueNumber))) {
                        IssueReport issue = new IssueReport();
                        issue.setId(issueNumber);
                        issue.setTitre(issueNode.get("title").asText());

                        JsonNode bodyNode = issueNode.get("body");
                        issue.setContenu(bodyNode != null && !bodyNode.isNull() ? bodyNode.asText() : "");

                        issue.setEtat(issueNode.get("state").asText());

                        String createdAt = issueNode.get("created_at").asText();
                        issue.setCreatedAt(LocalDateTime.parse(createdAt.replace("Z", ""),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

                        // Gestion de l'utilisateur créateur
                        JsonNode userNode = issueNode.get("user");
                        if (userNode != null) {
                            String userLogin = userNode.get("login").asText();
                            Utilisateur utilisateur = getOrCreateUserByLogin(userLogin);
                            issue.setUtilisateurId(utilisateur.getId());
                        }

                        issueRepository.save(issue);
                        logger.debug("Issue sauvegardée: #{}", issueNumber);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la synchronisation des issues", e);
        }
    }

    private void syncPullRequests() {
        try {
            logger.info("Synchronisation des pull requests...");
            String url = String.format("https://api.github.com/repos/%s/%s/pulls?state=all", repoOwner, repoName);

            HttpEntity<String> entity = new HttpEntity<>(createHeaders());
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode pullRequests = objectMapper.readTree(response.getBody());

                for (JsonNode prNode : pullRequests) {
                    Long prNumber = prNode.get("number").asLong();

                    // Vérifier si la PR existe déjà
                    if (pullRequestRepository.findAll().stream().noneMatch(pr -> pr.getId().equals(prNumber))) {
                        PullRequestReport pullRequest = new PullRequestReport();
                        pullRequest.setId(prNumber);
                        pullRequest.setTitre(prNode.get("title").asText());
                        pullRequest.setEtat(prNode.get("state").asText());

                        String createdAt = prNode.get("created_at").asText();
                        pullRequest.setCreatedAt(LocalDateTime.parse(createdAt.replace("Z", ""),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));

                        // Gestion de l'utilisateur créateur
                        JsonNode userNode = prNode.get("user");
                        if (userNode != null) {
                            String userLogin = userNode.get("login").asText();
                            Utilisateur utilisateur = getOrCreateUserByLogin(userLogin);
                            pullRequest.setUtilisateurId(utilisateur.getId());
                        }

                        pullRequestRepository.save(pullRequest);
                        logger.debug("Pull Request sauvegardée: #{}", prNumber);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la synchronisation des pull requests", e);
        }
    }

    private Utilisateur getOrCreateUser(String name, String email) {
        // Essayer de trouver par email d'abord
        Optional<Utilisateur> existing = utilisateurRepository.findByLogin(email);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Créer un nouvel utilisateur
        Utilisateur utilisateur = new Utilisateur();

        // Séparer le nom complet en nom et prénom
        String[] nameParts = name.split(" ", 2);
        utilisateur.setPrenom(nameParts[0]);
        utilisateur.setNom(nameParts.length > 1 ? nameParts[1] : "");
        utilisateur.setLogin(email);

        return utilisateurRepository.save(utilisateur);
    }

    private Utilisateur getOrCreateUserByLogin(String login) {
        Optional<Utilisateur> existing = utilisateurRepository.findByLogin(login);

        if (existing.isPresent()) {
            return existing.get();
        }

        // Créer un nouvel utilisateur avec le login GitHub
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(login);
        utilisateur.setNom("");
        utilisateur.setLogin(login);

        return utilisateurRepository.save(utilisateur);
    }

    // Méthode pour synchronisation manuelle
    public void forceSyncNow() {
        logger.info("Synchronisation manuelle demandée");
        syncGitHubData();
    }
}