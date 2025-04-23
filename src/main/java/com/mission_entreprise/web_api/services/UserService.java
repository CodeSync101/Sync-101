package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.entities.GitHubUser;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GitHubApiClient apiClient;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Value("${base_url}")
    private  String githubBaseUrl ;


    public GitHubUser getUser(String username) {
        String url = githubBaseUrl + "/users/" + username;
        try {
            return apiClient.fetchFromGitHub(url, GitHubUser.class);
        } catch (Exception e) {
            logger.error("Error fetching GitHub user for username: {}", username, e);
            throw new RuntimeException("Unable to fetch GitHub user details", e);
        }
    }

}
