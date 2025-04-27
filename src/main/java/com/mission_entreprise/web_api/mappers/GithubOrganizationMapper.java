package com.mission_entreprise.web_api.mappers;

import com.mission_entreprise.web_api.entities.GithubOrganization;
import java.time.Instant;
import java.util.Map;

public class GithubOrganizationMapper {

    public static GithubOrganization mapToEntity(Map<String, Object> apiResponse) {
        GithubOrganization githubOrganization = new GithubOrganization();

        githubOrganization.setLogin((String) apiResponse.get("login"));
        githubOrganization.setUrl((String) apiResponse.get("url"));
        githubOrganization.setAvatarUrl((String) apiResponse.get("avatar_url"));
        githubOrganization.setReposUrl((String) apiResponse.get("repos_url"));
        githubOrganization.setEventsUrl((String) apiResponse.get("events_url"));
        githubOrganization.setIssuesUrl((String) apiResponse.get("issues_url"));
        githubOrganization.setMembersUrl((String) apiResponse.get("members_url"));
        githubOrganization.setPublicMembersUrl((String) apiResponse.get("public_members_url"));

        // Optional fields - can be null
        githubOrganization.setDescription((String) apiResponse.get("description"));
        githubOrganization.setIsVerified((Boolean) apiResponse.get("is_verified"));

        // Converting String to Instant for dates
        githubOrganization.setCreatedAt(Instant.parse((String) apiResponse.get("created_at")));
        githubOrganization.setUpdatedAt(Instant.parse((String) apiResponse.get("updated_at")));
        if (apiResponse.get("archived_at") != null) {
            githubOrganization.setArchivedAt(Instant.parse((String) apiResponse.get("archived_at")));
        }

        githubOrganization.setHasOrganizationProjects((Boolean) apiResponse.get("has_organization_projects"));
        githubOrganization.setHasRepositoryProjects((Boolean) apiResponse.get("has_repository_projects"));
        githubOrganization.setPublicRepos((Integer) apiResponse.get("public_repos"));
        githubOrganization.setFollowers((Integer) apiResponse.get("followers"));
        githubOrganization.setFollowing((Integer) apiResponse.get("following"));
        githubOrganization.setHtmlUrl((String) apiResponse.get("html_url"));

        githubOrganization.setTotalPrivateRepos((Integer) apiResponse.get("total_private_repos"));
        githubOrganization.setOwnedPrivateRepos((Integer) apiResponse.get("owned_private_repos"));

        githubOrganization.setCollaborators((Integer) apiResponse.get("collaborators"));
        githubOrganization.setDefaultRepositoryPermission((String) apiResponse.get("default_repository_permission"));

        return githubOrganization;
    }
}
