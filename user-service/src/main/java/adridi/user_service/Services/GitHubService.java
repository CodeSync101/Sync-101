package adridi.user_service.Services;

import adridi.user_service.Config.GitHubProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubService {
    private final GitHubProperties gitHubProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    public void inviteUserToRepo(String repoOwner, String repoName, String username) {
        try {
            log.debug("Attempting to invite user {} to repo {}/{}", username, repoOwner, repoName);

            // Clean up repository name
            String cleanRepoName = repoName
                    .replace(".git", "")
                    .replace(" ", "-")
                    .replace("_", "-");

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Accept", "application/vnd.github+json");
            headers.set("Authorization", "Bearer " + gitHubProperties.getToken());
            headers.set("X-GitHub-Api-Version", "2022-11-28");

            // Add collaborator using username
            String repoUrl = String.format("https://api.github.com/repos/%s/%s", repoOwner, cleanRepoName);
            String collaboratorUrl = repoUrl + "/collaborators/" + username;

            HttpEntity<String> putEntity = new HttpEntity<>("{\"permission\":\"write\"}", headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    collaboratorUrl,
                    HttpMethod.PUT,
                    putEntity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                log.info("Successfully sent invitation to GitHub user: {}", username);
            } else if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
                log.info("User {} is already a collaborator", username);
            } else {
                throw new RuntimeException("Failed to invite user: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("Failed to send GitHub invitation: {}", e.getMessage());
            throw new RuntimeException("Failed to send GitHub invitation: " + e.getMessage());
        }
    }
}