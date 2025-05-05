package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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


}
