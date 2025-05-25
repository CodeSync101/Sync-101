package com.mission_entreprise.web_api.controllers;


import com.mission_entreprise.web_api.dtos.EventAnalyticsDTO;
import com.mission_entreprise.web_api.dtos.PullMergeDTO;
import com.mission_entreprise.web_api.dtos.PushEventDTO;
import com.mission_entreprise.web_api.services.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reporting")
@RequiredArgsConstructor
public class ReportingController {

    private final CommitService commitService;


    @GetMapping("/get-latest-commits")
    public ResponseEntity<List<EventAnalyticsDTO>> getLatestCommits(){
        try {
            List<EventAnalyticsDTO> commits = commitService.getAllCommitsAnalyticsLatest();
            return ResponseEntity.ok(commits);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-latest-pushs")
    public ResponseEntity<List<PushEventDTO>> getLatestPush(){
        try {
            List<PushEventDTO> pushes = commitService.getAllPushAnalyticsLatest();
            return ResponseEntity.ok(pushes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/get-latest-pulls")
    public ResponseEntity<List<PullMergeDTO>> getLatestPulls(){
        try {
            List<PullMergeDTO> pushes = commitService.getAllPullAnalyticsLatest();
            return ResponseEntity.ok(pushes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
