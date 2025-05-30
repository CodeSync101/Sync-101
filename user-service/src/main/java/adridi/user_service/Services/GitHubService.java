package adridi.user_service.Services;

public interface GitHubService {

    void inviteUserToRepo(String owner, String repoName, String githubUsername, String permission);
}