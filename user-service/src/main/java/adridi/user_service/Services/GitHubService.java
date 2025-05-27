package adridi.user_service.Services;

public interface GitHubService {
    /**
     * Invites a GitHub user to a repository.
     *
     * @param owner Repository owner's GitHub username
     * @param repoName Repository name
     * @param githubUsername GitHub username of the user to invite
     * @param permission Permission level ("pull", "push", "admin", "maintain", "triage")
     * @throws RuntimeException if invitation fails
     */
    void inviteUserToRepo(String owner, String repoName, String githubUsername, String permission);
}