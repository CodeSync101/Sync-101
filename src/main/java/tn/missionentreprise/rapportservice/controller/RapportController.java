package tn.missionentreprise.rapportservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.missionentreprise.rapportservice.repositories.UtilisateurRepository;
import tn.missionentreprise.rapportservice.services.RapportService;
import tn.missionentreprise.rapportservice.Entity.Utilisateur;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rapport")
@CrossOrigin(origins = "http://localhost:4200") // Assure la communication avec Angular
public class RapportController {

    @Autowired
    private RapportService rapportService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> generatePdfRapport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<String> selectedStudents,
            @RequestParam(required = false) List<String> selectedSections) {

        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : null;

        ByteArrayInputStream bis = rapportService.generatePdfRapport(start, end, selectedStudents, selectedSections);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=rapport.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    @GetMapping("/etudiants")
    public List<String> getEtudiants() {
        System.out.println(">>> [API] getEtudiants appelé"); // Pour debug
        return utilisateurRepository.findAll().stream()
                .map(u -> (u.getNom() + " " + u.getPrenom()).trim())
                .collect(Collectors.toList());
    }
}
