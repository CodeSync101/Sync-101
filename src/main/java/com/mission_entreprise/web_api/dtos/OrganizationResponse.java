package com.mission_entreprise.web_api.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrganizationResponse {

    private Long id;
    private String login;
    private String url;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    @JsonProperty("repos_url")
    private String reposUrl;

    @JsonProperty("events_url")
    private String eventsUrl;

    @JsonProperty("issues_url")
    private String issuesUrl;

    @JsonProperty("members_url")
    private String membersUrl;

    @JsonProperty("public_members_url")
    private String publicMembersUrl;

    private String description;

    @JsonProperty("is_verified")
    private Boolean isVerified;

    @JsonProperty("created_at")
    private String createdAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @JsonProperty("archived_at")
    private String archivedAt;

    @JsonProperty("has_organization_projects")
    private Boolean hasOrganizationProjects;

    @JsonProperty("has_repository_projects")
    private Boolean hasRepositoryProjects;

    @JsonProperty("public_repos")
    private Integer publicRepos;

    private Integer followers;
    private Integer following;

    @JsonProperty("html_url")
    private String htmlUrl;

    @JsonProperty("total_private_repos")
    private Integer totalPrivateRepos;

    @JsonProperty("owned_private_repos")
    private Integer ownedPrivateRepos;

    private Integer collaborators;

    @JsonProperty("default_repository_permission")
    private String defaultRepositoryPermission;
}