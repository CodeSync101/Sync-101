package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/commit")
public class CommitController {

    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @PostMapping("/save-commits")
    public ResponseEntity<?> saveCommits(@RequestParam String orgName, @RequestParam String repoName) {
        commitService.saveCommitsByOrgAndRepo(orgName, repoName);
        return new ResponseEntity<>("Commits saved", HttpStatus.OK);
    }
    @GetMapping("/commits-by-branch")
    public ResponseEntity<?> getCommitsByBranch(
            @RequestParam String orgName,
            @RequestParam String repoName,
            @RequestParam String branchName) {
        commitService.saveCommitsByOrgAndRepoAndBranch(orgName, repoName, branchName);
        return new ResponseEntity<>("Commits for branch fetched and saved", HttpStatus.OK);
    }
}
