package tn.missionentreprise.rapportservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.missionentreprise.rapportservice.services.RapportService;

import java.io.ByteArrayInputStream;
@RestController
@RequestMapping("/api/rapport")
public class RapportController {

    @Autowired
    private RapportService rapportService;

    @GetMapping(value = "/commits", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdfRapport() {
        ByteArrayInputStream bis = rapportService.generatePdfRapport();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=commits_par_etudiant.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
