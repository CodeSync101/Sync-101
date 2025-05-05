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
@RequestMapping("/api/branches")
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
    public ResponseEntity<Map<String, Object>> getDistinctBranchCount() {
        long count = branchService.getDistinctBranchCount();
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("distinctBranchCount", count);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
