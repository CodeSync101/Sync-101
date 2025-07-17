package com.mission_entreprise.web_api.utils;

import com.mission_entreprise.web_api.dtos.BranchResponse;
import com.mission_entreprise.web_api.dtos.CommitResponse;
import com.mission_entreprise.web_api.dtos.PullResponse;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching data from GitHub API: {}", e.getMessage());
            throw e;
        }
    }

    public CommitResponse[] fetchCommitsByOrgAndRepo(String org, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits", org, repo);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<CommitResponse[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, CommitResponse[].class);
            return responseEntity.getBody(); // Returns the list of commits
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching data from GitHub API: {}", e.getMessage());
            throw e;
        }
    }
    public CommitResponse[] fetchCommitsByOrgAndRepoAndBranch(String org, String repo, String branch) {
        String url = String.format("https://api.github.com/repos/%s/%s/commits?sha=%s", org, repo, branch);
        log.info(url);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<CommitResponse[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, CommitResponse[].class);
            return responseEntity.getBody(); // Returns the list of commits
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching commits for branch '{}' from GitHub API: {}", branch, e.getMessage());
            throw e;
        }
    }
    public BranchResponse[] fetchBranchesByOrgAndRepo(String org, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/branches", org, repo);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<BranchResponse[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, BranchResponse[].class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching branches from GitHub API: {}", e.getMessage());
            throw e;
        }
    }
    public PullResponse[] fetchPullRequestsByOrgAndRepo(String org, String repo) {
        String url = String.format("https://api.github.com/repos/%s/%s/pulls?state=all" +
                "", org, repo);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(githubToken);
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<PullResponse[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, PullResponse[].class);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Error fetching pull requests: {}", e.getMessage());
            throw e;
        }
    }




}
