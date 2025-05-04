package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.PullService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pulls")
public class PullController {

    private final PullService pullService;

    @Autowired
    public PullController(PullService pullService) {
        this.pullService = pullService;
    }

    @GetMapping("/save")
    public String savePulls(@RequestParam String org, @RequestParam String repo) {
        pullService.savePullRequestsByOrgAndRepo(org, repo);
        return "Pull requests fetched and saved successfully";
    }
}
