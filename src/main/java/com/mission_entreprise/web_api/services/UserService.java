package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.entities.GitHubUser;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final GitHubApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(GitHubApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public GitHubUser getUser(String username) {
        String url = "https://api.github.com/users/" + username;
        try {
            return apiClient.fetchFromGitHub(url, GitHubUser.class);
        } catch (Exception e) {
            logger.error("Error fetching GitHub user for username: {}", username, e);
            throw new RuntimeException("Unable to fetch GitHub user details", e);
        }
    }
}
