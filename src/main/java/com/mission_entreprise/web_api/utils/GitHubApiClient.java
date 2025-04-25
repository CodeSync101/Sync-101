package com.mission_entreprise.web_api.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@Service
public class GitHubApiClient {

    @Value("${github.token}")
    private String githubToken;

    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(GitHubApiClient.class);

    public GitHubApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> T fetchFromGitHub(String url, Class<T> responseType) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("The GitHub API URL must not be null or empty.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github+json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<T> response = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching data from GitHub API for URL: {}", url, e);
            throw new RuntimeException("GitHub API request failed: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("Unexpected error fetching data from GitHub API for URL: {}", url, e);
            throw new RuntimeException("Unexpected error occurred while calling GitHub API", e);
        }
    }

}
