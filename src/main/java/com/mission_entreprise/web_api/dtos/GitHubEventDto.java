package com.mission_entreprise.web_api.dtos;

import java.time.ZonedDateTime;

public class GitHubEventDto {
    private String type;
    private String actorLogin;
    private String repoName;
    private String authorName;
    private String commitMessage;
    private ZonedDateTime createdAt;

    // Default constructor
    public GitHubEventDto() {
    }

    // Constructor with all fields
    public GitHubEventDto(String type, String actorLogin, String repoName,
                          String authorName, String commitMessage, ZonedDateTime createdAt) {
        this.type = type;
        this.actorLogin = actorLogin;
        this.repoName = repoName;
        this.authorName = authorName;
        this.commitMessage = commitMessage;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActorLogin() {
        return actorLogin;
    }

    public void setActorLogin(String actorLogin) {
        this.actorLogin = actorLogin;
    }

    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "GitHubEventDto{" +
                "type='" + type + '\'' +
                ", actorLogin='" + actorLogin + '\'' +
                ", repoName='" + repoName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", commitMessage='" + commitMessage + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}