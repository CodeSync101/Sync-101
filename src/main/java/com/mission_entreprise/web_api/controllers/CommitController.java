package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commit")
public class CommitController {

    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @PostMapping("/save-commits")
    public ResponseEntity<Map<String, Object>> saveCommits(
            @RequestParam String orgName,
            @RequestParam String repoName) {
        commitService.saveCommitsByOrgAndRepo(orgName, repoName);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Commits saved");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/commits-by-branch")
    public ResponseEntity<Map<String, Object>> getCommitsByBranch(
            @RequestParam String orgName,
            @RequestParam String repoName,
            @RequestParam String branchName) {
        commitService.saveCommitsByOrgAndRepoAndBranch(orgName, repoName, branchName);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Commits for branch fetched and saved");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/total-count")
    public ResponseEntity<Map<String, Object>> getTotalCommitsCount() {
        long count = commitService.getTotalCommitsCount();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("totalCommits", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/distinct-authors")
    public ResponseEntity<Map<String, Object>> getDistinctCommitAuthors() {
        Map<String, Object> authorsWithCount = commitService.getDistinctAuthorsWithCount();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("authors", authorsWithCount.get("authors"));
        response.put("authorsCount", authorsWithCount.get("authorsCount"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/distinct-repositories")
    public ResponseEntity<Map<String, Object>> getDistinctRepositories() {
        Map<String, Object> repoData = commitService.getDistinctRepositoriesWithCount();

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("repositories", repoData.get("repositories"));
        response.put("repositoryCount", repoData.get("repositoryCount"));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/commits-by-collaborator")
    public ResponseEntity<Map<String, Long>> getCommitCountsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(required = false, defaultValue = "") String author) {

        try {
            Map<String, Long> commitCounts = commitService.getCommitCountsByDateRange(startDate, endDate, author);
            return ResponseEntity.ok(commitCounts);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
