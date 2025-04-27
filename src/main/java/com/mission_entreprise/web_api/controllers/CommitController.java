package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.CommitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/commit")
public class CommitController {

    private final CommitService commitService;

    @Autowired
    public CommitController(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/save-commits/{orgName}/{repoName}")
    public String saveCommits(@PathVariable String orgName, @PathVariable String repoName) {
        commitService.saveCommitsByOrgAndRepo(orgName, repoName);
        return "Commits saved successfully!";
    }
}
