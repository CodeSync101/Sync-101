package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.exceptions.PullNotFound;
import com.mission_entreprise.web_api.services.PullService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> savePulls(@RequestParam String org, @RequestParam String repo) {
        try {
            pullService.savePullRequestsByOrgAndRepo(org, repo);
            return new ResponseEntity<>("Pull requests fetched and saved successfully", HttpStatus.OK);

        }catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
