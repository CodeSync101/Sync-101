package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Slf4j
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/sync")
    public ResponseEntity<String> syncBranches(@RequestParam String org, @RequestParam String repo) {
        try {
            branchService.syncBranches(org, repo);
            return ResponseEntity.ok("Branches synced successfully.");
        } catch (Exception e) {
            log.error("Error syncing branches", e);
            return ResponseEntity.status(500).body("Error syncing branches.");
        }
    }
}
