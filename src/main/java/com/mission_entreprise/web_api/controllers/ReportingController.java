package com.mission_entreprise.web_api.controllers;


import com.mission_entreprise.web_api.dtos.ContributionSummaryDTO;
import com.mission_entreprise.web_api.dtos.EventAnalyticsDTO;
import com.mission_entreprise.web_api.dtos.PullMergeDTO;
import com.mission_entreprise.web_api.dtos.PushEventDTO;
import com.mission_entreprise.web_api.services.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final CommitService commitService;


    @GetMapping("/get-latest-commits")
    public ResponseEntity<List<EventAnalyticsDTO>> getLatestCommits(@RequestParam String organization) {
        try {
            List<EventAnalyticsDTO> commits = commitService.getAllCommitsAnalyticsLatest(organization);
            return ResponseEntity.ok(commits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-latest-pushs")
    public ResponseEntity<List<PushEventDTO>> getLatestPush(@RequestParam String organization) {
        try {
            List<PushEventDTO> pushes = commitService.getAllPushAnalyticsLatest(organization);
            return ResponseEntity.ok(pushes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/get-latest-pulls")
    public ResponseEntity<List<PullMergeDTO>> getLatestPulls() {
        try {
            List<PullMergeDTO> pushes = commitService.getAllPullAnalyticsLatest();
            return ResponseEntity.ok(pushes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/contribution-summary")
    public ResponseEntity<ContributionSummaryDTO> getContributionSummary(@RequestParam String organization) {
        try {
            int topLimit = 5 ;
            ContributionSummaryDTO summary = commitService.getContributionSummary(topLimit,organization);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
