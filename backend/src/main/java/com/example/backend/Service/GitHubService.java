package com.example.backend.Service;

import com.example.backend.Entity.*;
import com.example.backend.Repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class GitHubService {

    private final String GITHUB_API = "https://api.github.com";
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired private RepositoryRepository repositoryRepo;
    @Autowired private BranchRepository branchRepo;
    @Autowired private CommitRepository commitRepo;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private PullRepository pullRepo;

    private HttpEntity<String> buildRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }

    @Transactional
    public void syncAllFromOrg(String orgName, String token) {
        HttpEntity<String> request = buildRequest(token);

        // Delete old data
        repositoryRepo.deleteAll();    // Repositories are top-level
        pullRepo.deleteAll();    // Repositories are top-level
        branchRepo.deleteAll();    // Repositories are top-level
        commitRepo.deleteAll();    // Repositories are top-level
        authorRepository.deleteAll();    // Repositories are top-level




        // Fetch repositories
        String reposUrl = GITHUB_API + "/orgs/" + orgName + "/repos?per_page=100";
        ResponseEntity<List> reposResp = restTemplate.exchange(reposUrl, HttpMethod.GET, request, List.class);
        List<Map<String, Object>> repos = reposResp.getBody();
        if (repos == null) return;

        for (Map<String, Object> repoData : repos) {
            String repoName = (String) repoData.get("name");
            String owner = ((Map<String, Object>) repoData.get("owner")).get("login").toString();
            String defaultBranch = (String) repoData.get("default_branch");

            Repository repoEntity = new Repository();
            repoEntity.setName(repoName);
            repoEntity.setOwner(owner);
            repositoryRepo.save(repoEntity);

            // Fetch branches
            String branchesUrl = GITHUB_API + "/repos/" + owner + "/" + repoName + "/branches";
            ResponseEntity<List> branchesResp = restTemplate.exchange(branchesUrl, HttpMethod.GET, request, List.class);
            List<Map<String, Object>> branches = branchesResp.getBody();
            if (branches == null) continue;

            for (Map<String, Object> branchData : branches) {
                String branchName = (String) branchData.get("name");

                Branch branchEntity = new Branch();
                branchEntity.setName(branchName);
                branchEntity.setRepository(repoEntity);

                // Check if merged into default branch
                boolean isMerged = false;
                if (!branchName.equals(defaultBranch)) {
                    String compareUrl = GITHUB_API + "/repos/" + owner + "/" + repoName + "/compare/" + defaultBranch + "..." + branchName;
                    try {
                        ResponseEntity<Map> compareResp = restTemplate.exchange(compareUrl, HttpMethod.GET, request, Map.class);
                        Map<String, Object> compareData = compareResp.getBody();
                        if (compareData != null) {
                            String status = (String) compareData.get("status");
                            // If defaultBranch is behind or identical to branch → branch is merged
                            isMerged = "behind".equals(status) || "identical".equals(status);
                        }

                    } catch (Exception e) {
                        System.out.println("Compare failed for " + branchName + " → " + defaultBranch + ": " + e.getMessage());
                    }
                }

                branchEntity.setIsMerged(isMerged);
                branchRepo.save(branchEntity);

                // Fetch commits
                String commitsUrl = GITHUB_API + "/repos/" + owner + "/" + repoName + "/commits?sha=" + branchName + "&per_page=30";
                ResponseEntity<List> commitsResp = restTemplate.exchange(commitsUrl, HttpMethod.GET, request, List.class);
                List<Map<String, Object>> commits = commitsResp.getBody();
                if (commits == null) continue;

                for (Map<String, Object> commitData : commits) {
                    String sha = (String) commitData.get("sha");
                    Map<String, Object> commitDetails = (Map<String, Object>) commitData.get("commit");
                    String message = (String) commitDetails.get("message");

                    String authorName;
                    String authorEmail;

                    if (commitDetails.containsKey("author")) {
                        Map<String, Object> authorData = (Map<String, Object>) commitDetails.get("author");
                        if (authorData != null) {
                            authorName = authorData.get("name") != null ? authorData.get("name").toString() : "Unknown";
                            authorEmail = authorData.get("email") != null ? authorData.get("email").toString() : null;
                        } else {
                            authorEmail = null;
                            authorName = "Unknown";
                        }
                    } else {
                        authorEmail = null;
                        authorName = "Unknown";
                    }

                    Author author = authorRepository.findByName(authorName)
                            .orElseGet(() -> {
                                Author newAuthor = new Author();
                                newAuthor.setName(authorName);
                                newAuthor.setEmail(authorEmail);
                                return authorRepository.save(newAuthor);
                            });

                    Commit commitEntity = new Commit();
                    commitEntity.setSha(sha);
                    commitEntity.setMessage(message.length() > 255 ? message.substring(0, 255) : message); // avoid DB error
                    commitEntity.setAuthor(author);
                    commitEntity.setBranch(branchEntity);
                    commitRepo.save(commitEntity);
                }
            }

            // Fetch pull requests
            String pullsUrl = GITHUB_API + "/repos/" + owner + "/" + repoName + "/pulls?state=all";
            ResponseEntity<List> pullsResp = restTemplate.exchange(pullsUrl, HttpMethod.GET, request, List.class);
            List<Map<String, Object>> pulls = pullsResp.getBody();
            if (pulls == null) continue;

            for (Map<String, Object> pull : pulls) {
                Pull pullEntity = new Pull();
                pullEntity.setTitle((String) pull.get("title"));
                pullEntity.setState((String) pull.get("state"));
                pullEntity.setRepository(repoEntity);
                pullRepo.save(pullEntity);
            }
        }
    }
}
