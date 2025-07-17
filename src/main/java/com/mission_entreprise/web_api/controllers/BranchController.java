package com.mission_entreprise.web_api.controllers;

import com.mission_entreprise.web_api.services.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/branches")
@RequiredArgsConstructor
@Slf4j
public class BranchController {

    private final BranchService branchService;

    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> syncBranches(@RequestParam String org, @RequestParam String repo) {
        Map<String, Object> response = new HashMap<>();
        try {
            branchService.syncBranches(org, repo);
            response.put("status", "success");
            response.put("message", "Branches synced successfully.");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error syncing branches", e);
            response.put("status", "error");
            response.put("message", "Error syncing branches.");
            return ResponseEntity.status(500).body(response);
        }
    }
    @GetMapping("/distinct-count")
    public ResponseEntity<Map<String, Object>> getDistinctBranchCount(@RequestParam String organization) {
        long count = branchService.getDistinctBranchCount(organization);
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("distinctBranchCount", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/fetch/{organization}")
    public ResponseEntity<String> fetchBranchesForOrganization(@PathVariable String organization) {
        if (organization == null || organization.isBlank()) {
            return ResponseEntity.badRequest().body("Organization name must be provided");
        }
        try {
            branchService.fetchAndSaveBranchesForOrg(organization);
            return ResponseEntity.ok("Branches fetched and saved for organization: " + organization);
        } catch (Exception e) {
            log.error("Error fetching branches for organization {}: {}", organization, e.getMessage());
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

}
