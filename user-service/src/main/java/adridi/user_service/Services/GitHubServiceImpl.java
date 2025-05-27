package adridi.user_service.Services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class GitHubServiceImpl implements GitHubService {

    private final RestTemplate restTemplate;
    private final String githubApiUrl;
    private final String githubToken;

    public GitHubServiceImpl(
            RestTemplate restTemplate,
            @Value("${github.api.url}") String githubApiUrl,
            @Value("${github.api.token}") String githubToken) {
        this.restTemplate = restTemplate;
        this.githubApiUrl = githubApiUrl;
        this.githubToken = githubToken;
    }

    @Override
    public void inviteUserToRepo(String owner, String repoName, String githubUsername, String permission) {
        String url = String.format("%s/repos/%s/%s/collaborators/%s",
                githubApiUrl, owner, repoName, githubUsername);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        headers.set("X-GitHub-Api-Version", "2022-11-28");

        // Create request body with permission
        String requestBody = String.format("{\"permission\":\"%s\"}", permission);

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            log.debug("Sending GitHub invitation to user {} for repo {}/{}",
                    githubUsername, owner, repoName);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    String.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to send GitHub invitation: " +
                        response.getStatusCode() + " - " + response.getBody());
            }

            log.debug("Successfully sent GitHub invitation");
        } catch (Exception e) {
            log.error("Failed to send GitHub invitation: {}", e.getMessage());
            throw new RuntimeException("Failed to send GitHub invitation: " + e.getMessage());
        }
    }
}