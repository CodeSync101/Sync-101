package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.dtos.GitHubEventDto;
import com.mission_entreprise.web_api.services.GitHubEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class GitHubEventController {

    private final GitHubEventService gitHubEventService;

    public GitHubEventController(GitHubEventService gitHubEventService) {
        this.gitHubEventService = gitHubEventService;
    }



    @GetMapping("/orgs/{orgName}/events")
    public ResponseEntity<List<GitHubEventDto>> getOrganizationEvents(@PathVariable String orgName) {
        List<GitHubEventDto> events = gitHubEventService.fetchOrganizationEvents(orgName);
        return ResponseEntity.ok(events);
    }


    @GetMapping("/repos/{orgName}/{repoName}/events")
    public ResponseEntity<List<GitHubEventDto>> getRepositoryEvents(
            @PathVariable String orgName,
            @PathVariable String repoName) {
        List<GitHubEventDto> events = gitHubEventService.fetchOrgRepoEvents(orgName, repoName);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/repos/{orgName}/{repoName}/events/saved")
    public ResponseEntity<List<GitHubEventDto>> getSavedRepositoryEvents(
            @PathVariable String orgName,
            @PathVariable String repoName) {
        String fullRepoName = orgName + "/" + repoName;
        List<GitHubEventDto> events = gitHubEventService.getSavedEventsByRepo(fullRepoName);
        return ResponseEntity.ok(events);
    }
}