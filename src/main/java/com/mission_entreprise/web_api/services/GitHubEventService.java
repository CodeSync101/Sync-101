package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.GitHubEventDto;
import com.mission_entreprise.web_api.entities.GitHubEvent;
import com.mission_entreprise.web_api.entities.GitHubEventEntity;
import com.mission_entreprise.web_api.repositories.GitHubEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubEventService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubEventService.class);

    private final RestTemplate restTemplate;
    private final GitHubEventRepository gitHubEventRepository;

    @Value("${github.api.token:}")
    private String githubToken;

    @Value("${github.api.events.url:https://api.github.com/events}")
    private String githubEventsUrl;

    public GitHubEventService(RestTemplate restTemplate, GitHubEventRepository gitHubEventRepository) {
        this.restTemplate = restTemplate;
        this.gitHubEventRepository = gitHubEventRepository;
    }
    public List<GitHubEventDto> fetchGitHubEvents() {
        logger.info("Fetching GitHub events from {}", githubEventsUrl);

        HttpHeaders headers = new HttpHeaders();
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.setBearerAuth(githubToken);
        }
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GitHubEvent[]> response = restTemplate.exchange(
                    githubEventsUrl,
                    HttpMethod.GET,
                    entity,
                    GitHubEvent[].class
            );

            GitHubEvent[] events = response.getBody();

            if (events == null) {
                logger.warn("No events were returned from GitHub API");
                return new ArrayList<>();
            }

            logger.info("Successfully fetched {} GitHub events", events.length);
            return mapToEventDtos(events);

        } catch (Exception e) {
            logger.error("Error fetching GitHub events: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to fetch GitHub events", e);
        }
    }
    public List<GitHubEventDto> fetchOrganizationEvents(String orgName) {
        if (orgName == null || orgName.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name cannot be null or empty");
        }

        String orgEventsUrl = String.format("https://api.github.com/orgs/%s/events", orgName);
        logger.info("Fetching events for organization {} from {}", orgName, orgEventsUrl);

        HttpHeaders headers = new HttpHeaders();
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.setBearerAuth(githubToken);
        }
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GitHubEvent[]> response = restTemplate.exchange(
                    orgEventsUrl,
                    HttpMethod.GET,
                    entity,
                    GitHubEvent[].class
            );

            GitHubEvent[] events = response.getBody();

            if (events == null) {
                logger.warn("No events were returned for organization {}", orgName);
                return new ArrayList<>();
            }

            logger.info("Successfully fetched {} events for organization {}", events.length, orgName);
            return mapToEventDtos(events);

        } catch (Exception e) {
            logger.error("Error fetching events for organization {}: {}", orgName, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events for organization " + orgName, e);
        }
    }
    @Transactional
    public List<GitHubEventDto> fetchOrgRepoEvents(String orgName, String repoName) {
        if (orgName == null || orgName.trim().isEmpty()) {
            throw new IllegalArgumentException("Organization name cannot be null or empty");
        }

        if (repoName == null || repoName.trim().isEmpty()) {
            throw new IllegalArgumentException("Repository name cannot be null or empty");
        }

        String repoEventsUrl = String.format("https://api.github.com/repos/%s/%s/events", orgName, repoName);
        logger.info("Fetching events for repository {}/{} from {}", orgName, repoName, repoEventsUrl);

        HttpHeaders headers = new HttpHeaders();
        if (githubToken != null && !githubToken.isEmpty()) {
            headers.setBearerAuth(githubToken);
        }
        headers.set("Accept", "application/vnd.github.v3+json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<GitHubEvent[]> response = restTemplate.exchange(
                    repoEventsUrl,
                    HttpMethod.GET,
                    entity,
                    GitHubEvent[].class
            );

            GitHubEvent[] events = response.getBody();

            if (events == null) {
                logger.warn("No events were returned for repository {}/{}", orgName, repoName);
                return new ArrayList<>();
            }

            List<GitHubEventDto> eventDtos = mapToEventDtos(events);

            // Save events to database
            saveEventsToDatabase(events, eventDtos);

            logger.info("Successfully fetched and saved {} events for repository {}/{}", events.length, orgName, repoName);
            return eventDtos;

        } catch (Exception e) {
            logger.error("Error fetching events for repository {}/{}: {}", orgName, repoName, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch events for repository " + orgName + "/" + repoName, e);
        }
    }
    private void saveEventsToDatabase(GitHubEvent[] events, List<GitHubEventDto> eventDtos) {
        logger.debug("Saving {} events to database", eventDtos.size());

        List<GitHubEventEntity> entitiesToSave = new ArrayList<>();

        for (int i = 0; i < events.length; i++) {
            GitHubEvent event = events[i];

            // Skip events that don't meet our criteria (no commits, etc.)
            if (event.getPayload() == null || event.getPayload().getCommits() == null ||
                    event.getPayload().getCommits().isEmpty()) {
                continue;
            }

            // Process each commit in the event
            for (GitHubEvent.Payload.Commit commit : event.getPayload().getCommits()) {
                // Check if this event already exists in the database
                if (!gitHubEventRepository.existsByEventId(event.getId() + "-" + commit.getSha())) {
                    GitHubEventEntity entity = new GitHubEventEntity(
                            event.getId() + "-" + commit.getSha(),
                            event.getType(),
                            event.getActor() != null ? event.getActor().getLogin() : null,
                            event.getRepo() != null ? event.getRepo().getName() : null,
                            commit.getAuthor() != null ? commit.getAuthor().getName() : null,
                            commit.getMessage(),
                            event.getCreatedAt()
                    );
                    entitiesToSave.add(entity);
                }
            }
        }

        if (!entitiesToSave.isEmpty()) {
            gitHubEventRepository.saveAll(entitiesToSave);
            logger.info("Saved {} new events to database", entitiesToSave.size());
        } else {
            logger.info("No new events to save to database");
        }
    }
    public List<GitHubEventDto> getSavedEventsByRepo(String repoName) {
        logger.info("Retrieving saved events for repository {}", repoName);
        List<GitHubEventEntity> entities = gitHubEventRepository.findByRepoNameOrderByCreatedAtDesc(repoName);

        return entities.stream()
                .map(entity -> new GitHubEventDto(
                        entity.getType(),
                        entity.getActorLogin(),
                        entity.getRepoName(),
                        entity.getAuthorName(),
                        entity.getCommitMessage(),
                        entity.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    private List<GitHubEventDto> mapToEventDtos(GitHubEvent[] events) {
        return Arrays.stream(events)
                .filter(event -> "PushEvent".equals(event.getType()) &&
                        event.getPayload() != null &&
                        event.getPayload().getCommits() != null &&
                        !event.getPayload().getCommits().isEmpty())
                .flatMap(event -> event.getPayload().getCommits().stream().map(commit -> {
                    GitHubEventDto dto = new GitHubEventDto();
                    dto.setType(event.getType());

                    if (event.getActor() != null) {
                        dto.setActorLogin(event.getActor().getLogin());
                    }

                    if (event.getRepo() != null) {
                        dto.setRepoName(event.getRepo().getName());
                    }

                    if (commit.getAuthor() != null) {
                        dto.setAuthorName(commit.getAuthor().getName());
                    }

                    dto.setCommitMessage(commit.getMessage());
                    dto.setCreatedAt(event.getCreatedAt());

                    return dto;
                }))
                .collect(Collectors.toList());
    }
}