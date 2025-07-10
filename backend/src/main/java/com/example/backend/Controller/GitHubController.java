package com.example.backend.Controller;

import com.example.backend.Service.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GitHubController {

    @Autowired
    private GitHubService gitHubService;
    @PostMapping("/sync-org")
    public String syncFromOrg(@RequestParam String org, @RequestParam String token) {
        gitHubService.syncAllFromOrg(org, token);
        return "Organization sync completed.";
    }

}
