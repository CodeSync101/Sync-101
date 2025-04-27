package com.mission_entreprise.web_api.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "organization")
@Getter
@Setter
public class GithubOrganization {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String login;
    private String url;
    private String avatarUrl;

    private String reposUrl;
    private String eventsUrl;

    private String issuesUrl;
    private String membersUrl;
    private String publicMembersUrl;

    private String description;
    private Boolean isVerified;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant archivedAt;


    private Boolean hasOrganizationProjects;
    private Boolean hasRepositoryProjects;

    private Integer publicRepos;

    private Integer followers;
    private Integer following;
    private String htmlUrl;


    private Integer totalPrivateRepos;
    private Integer ownedPrivateRepos;

    private Integer collaborators;
    private String defaultRepositoryPermission;


    @Version
    private Integer version;
}
