package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.CommitResponse;
import com.mission_entreprise.web_api.dtos.OrganizationResponse;
import com.mission_entreprise.web_api.entities.Author;
import com.mission_entreprise.web_api.entities.Commit;
import com.mission_entreprise.web_api.entities.GithubOrganization;
import com.mission_entreprise.web_api.repositories.AuthorRepository;
import com.mission_entreprise.web_api.repositories.CommitRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GithubService {

    @Value("${base_url}")
    private String baseUrl;

    private final GitHubApiClient apiClient;
    private final AuthorRepository authorRepository;
    private final CommitRepository commitRepository;

    public GithubOrganization getOrganization(String orgName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/orgs/{orgName}")
                    .buildAndExpand(orgName)
                    .toUriString();
            log.info("Fetching organization from GitHub: {}", url);

            OrganizationResponse apiResponse = apiClient.fetchFromGitHub(url, OrganizationResponse.class);

            GithubOrganization githubOrganization = new GithubOrganization();
            githubOrganization.setVersion(0);
            githubOrganization.setLogin(apiResponse.getLogin());
            githubOrganization.setUrl(apiResponse.getUrl());
            githubOrganization.setAvatarUrl(apiResponse.getAvatarUrl());
            githubOrganization.setReposUrl(apiResponse.getReposUrl());
            githubOrganization.setEventsUrl(apiResponse.getEventsUrl());
            githubOrganization.setIssuesUrl(apiResponse.getIssuesUrl());
            githubOrganization.setMembersUrl(apiResponse.getMembersUrl());
            githubOrganization.setPublicMembersUrl(apiResponse.getPublicMembersUrl());
            githubOrganization.setDescription(apiResponse.getDescription());
            githubOrganization.setIsVerified(apiResponse.getIsVerified());
            githubOrganization.setCreatedAt(parseInstant(apiResponse.getCreatedAt()));
            githubOrganization.setUpdatedAt(parseInstant(apiResponse.getUpdatedAt()));
            githubOrganization.setArchivedAt(parseInstant(apiResponse.getArchivedAt()));
            githubOrganization.setHasOrganizationProjects(apiResponse.getHasOrganizationProjects());
            githubOrganization.setHasRepositoryProjects(apiResponse.getHasRepositoryProjects());
            githubOrganization.setPublicRepos(apiResponse.getPublicRepos());
            githubOrganization.setFollowers(apiResponse.getFollowers());
            githubOrganization.setFollowing(apiResponse.getFollowing());
            githubOrganization.setHtmlUrl(apiResponse.getHtmlUrl());
            githubOrganization.setTotalPrivateRepos(apiResponse.getTotalPrivateRepos());
            githubOrganization.setOwnedPrivateRepos(apiResponse.getOwnedPrivateRepos());
            githubOrganization.setCollaborators(apiResponse.getCollaborators());
            githubOrganization.setDefaultRepositoryPermission(apiResponse.getDefaultRepositoryPermission());

            return githubOrganization;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while fetching GitHub organization", e);
        }
    }

    private Instant parseInstant(String dateString) {
        return (dateString != null) ? Instant.parse(dateString) : null;
    }

    public GithubOrganization getOrganizationRepositories(String orgName) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                    .path("/orgs/{orgName}/repos")
                    .buildAndExpand(orgName)
                    .toUriString();
            log.info("Fetching organization from GitHub: {}", url);

            OrganizationResponse apiResponse = apiClient.fetchFromGitHub(url, OrganizationResponse.class);

            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException("Error while fetching GitHub organization", e);
        }
    }


}
