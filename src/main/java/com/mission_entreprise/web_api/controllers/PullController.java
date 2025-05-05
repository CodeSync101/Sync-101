package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.PullService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pulls")
@RequiredArgsConstructor
@Slf4j
public class PullController {

    private final PullService pullService;

    @GetMapping("/save")
    public ResponseEntity<Map<String, Object>> savePulls(@RequestParam String org, @RequestParam String repo) {
        Map<String, Object> response = new HashMap<>();
        try {
            pullService.savePullRequestsByOrgAndRepo(org, repo);
            response.put("status", "success");
            response.put("message", "Pull requests fetched and saved successfully");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error saving pull requests", e);
            response.put("status", "error");
            response.put("message", "Internal server error while saving pull requests");
            return ResponseEntity.status(500).body(response);
        }
    }
}
