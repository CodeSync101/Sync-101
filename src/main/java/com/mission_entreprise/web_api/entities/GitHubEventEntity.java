package com.mission_entreprise.web_api.entities;

import jakarta.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "github_events")
public class GitHubEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String eventId;

    @Column(nullable = false)
    private String type;

    @Column(name = "organization")
    private String organization;

    @Column(name = "actor_login")
    private String actorLogin;

    @Column(name = "repo_name")
    private String repoName;

    @Column(name = "author_name")
    private String authorName;

    @Column(name = "commit_message", columnDefinition = "TEXT")
    private String commitMessage;

    @Column(name = "created_at")
    private ZonedDateTime createdAt;

    @Column(name = "saved_at")
    private ZonedDateTime savedAt;

    // Default constructor
    public GitHubEventEntity() {
    }
    @PrePersist
    @PreUpdate
    private void assignOrganization() {
        if (repoName != null && repoName.contains("/")) {
            this.organization = repoName.split("/")[0].trim();
        } else {
            this.organization = null;
        }
    }
    // Constructor from DTO
    public GitHubEventEntity(String eventId, String type, String actorLogin, String repoName,
                             String authorName, String commitMessage, ZonedDateTime createdAt) {
        this.eventId = eventId;
        this.type = type;
        this.actorLogin = actorLogin;
        this.repoName = repoName;
        this.authorName = authorName;
        this.commitMessage = commitMessage;
        this.createdAt = createdAt;
        this.savedAt = ZonedDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

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

    public ZonedDateTime getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(ZonedDateTime savedAt) {
        this.savedAt = savedAt;
    }

    @Override
    public String toString() {
        return "GitHubEventEntity{" +
                "id=" + id +
                ", eventId='" + eventId + '\'' +
                ", type='" + type + '\'' +
                ", actorLogin='" + actorLogin + '\'' +
                ", repoName='" + repoName + '\'' +
                ", authorName='" + authorName + '\'' +
                ", commitMessage='" + commitMessage + '\'' +
                ", createdAt=" + createdAt +
                ", savedAt=" + savedAt +
                '}';
    }
}