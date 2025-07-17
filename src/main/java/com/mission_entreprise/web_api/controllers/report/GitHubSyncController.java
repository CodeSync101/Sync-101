package com.mission_entreprise.web_api.controllers.report;

import com.mission_entreprise.web_api.services.report.GitHubDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/github")
public class GitHubSyncController {

    @Autowired
    private GitHubDataService gitHubDataService;

    @PostMapping("/sync")
    public ResponseEntity<String> forceSyncGitHubData() {
        try {
            gitHubDataService.forceSyncNow();
            return ResponseEntity.ok("Synchronisation GitHub démarrée avec succès");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Erreur lors de la synchronisation: " + e.getMessage());
        }
    }

    @GetMapping("/sync/status")
    public ResponseEntity<String> getSyncStatus() {
        return ResponseEntity.ok("Service de synchronisation GitHub actif. " +
                "Synchronisation automatique toutes les 30 minutes.");
    }
}