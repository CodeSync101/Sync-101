package adridi.user_service.Services;

import adridi.user_service.DTO.GitHubRepoRequest;
import adridi.user_service.DTO.GroupRepoRequest;
import adridi.user_service.Models.GroupRepo;
import adridi.user_service.Models.Organization;
import adridi.user_service.Repositories.GroupRepoRepository;
import adridi.user_service.Repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GroupRepoServiceImpl implements GroupRepoService {

    private final GroupRepoRepository groupRepoRepository;
    private final OrganizationRepository organizationRepository;
    private final RestTemplate restTemplate;

    @Value("${github.api.url}")
    private String githubApiUrl;

    @Value("${github.api.token}")
    private String githubApiToken;

    @Override
    public GroupRepo registerGroupRepo(GroupRepoRequest request) {
        Organization organization = organizationRepository.findById(request.getOrganizationId())
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        GroupRepo groupRepo = new GroupRepo(
                request.getGroup_name(),
                request.getGroup_description(),
                request.getGroup_type(),
                organization
        );
        GroupRepo savedGroup = groupRepoRepository.save(groupRepo);

        createGitHubRepository(request);

        return savedGroup;
    }

    private void createGitHubRepository(GroupRepoRequest request) {
        GitHubRepoRequest gitHubRequest = new GitHubRepoRequest(
                request.getGroup_name(),
                request.getGroup_description(),
                "private".equalsIgnoreCase(request.getGroup_type())
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + githubApiToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<GitHubRepoRequest> entity = new HttpEntity<>(gitHubRequest, headers);

        String url = githubApiUrl + "/user/repos";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to create GitHub repository: " + response.getStatusCode() + " - " + response.getBody());
        }
    }
}