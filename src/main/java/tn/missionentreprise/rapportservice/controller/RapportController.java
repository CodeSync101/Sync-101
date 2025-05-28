package tn.missionentreprise.rapportservice.controller;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.missionentreprise.rapportservice.services.RapportService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rapport")
public class RapportController {

    private static final Logger log = LoggerFactory.getLogger(RapportController.class);

    @Autowired
    private RapportService rapportService;

    @GetMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<InputStreamResource> getPdfRapport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) List<Long> userIds,
            @RequestParam(required = false, defaultValue = "Commits,Pull Requests,Issues,Branches,Tickets,Aide à la prise de décision") String selectedSections) {
        log.info("Appel de l'endpoint /generate avec startDate: {}, endDate: {}, userIds: {}, sections: {}",
                startDate, endDate, userIds, selectedSections);

        // Parse et validation des dates
        LocalDate parsedStartDate = null;
        LocalDate parsedEndDate = null;

        try {
            if (startDate != null && !startDate.trim().isEmpty()) {
                parsedStartDate = LocalDate.parse(startDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
            if (endDate != null && !endDate.trim().isEmpty()) {
                parsedEndDate = LocalDate.parse(endDate.trim(), DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (DateTimeParseException e) {
            log.error("Erreur lors du parsing des dates : {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new InputStreamResource(createErrorPdf("Dates invalides. Utilisez le format yyyy-MM-dd.")));
        }

        if (parsedStartDate != null && parsedEndDate != null && parsedStartDate.isAfter(parsedEndDate)) {
            log.warn("La date de début ({}) est postérieure à la date de fin ({}).", parsedStartDate, parsedEndDate);
            return ResponseEntity.badRequest()
                    .body(new InputStreamResource(createErrorPdf("La date de début doit être antérieure ou égale à la date de fin.")));
        }

        // Convertir LocalDate en Timestamp avec gestion des valeurs par défaut
        Timestamp startTimestamp = parsedStartDate != null ? Timestamp.valueOf(parsedStartDate.atStartOfDay()) : null;
        Timestamp endTimestamp = parsedEndDate != null ? Timestamp.valueOf(parsedEndDate.atTime(23, 59, 59)) : null;

        // Normaliser userIds
        List<Long> safeUserIds = (userIds != null) ? userIds.stream().filter(Objects::nonNull).collect(Collectors.toList()) : Collections.emptyList();
        log.debug("User IDs normalisés : {}", safeUserIds);

        // Préparer les sections
        Set<String> sectionsSet = (selectedSections != null && !selectedSections.trim().isEmpty()) ?
                Arrays.stream(selectedSections.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toSet()) : new HashSet<>();
        log.debug("Sections normalisées : {}", sectionsSet);
log.debug(String.valueOf(endTimestamp),"++++++++++++++++++++++++++++++++++++++++++++++++++");
        // Appeler le service avec les paramètres validés
        ByteArrayInputStream bis;
        try {
            bis = rapportService.generatePdfRapport(startTimestamp, endTimestamp, safeUserIds, sectionsSet);
        } catch (Exception e) {
            log.error("Erreur lors de la génération du rapport : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InputStreamResource(createErrorPdf("Erreur lors de la génération du rapport : "+parsedEndDate + e.getMessage())));
        }

        // Préparer la réponse
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=rapport.pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }

    private ByteArrayInputStream createErrorPdf(String message) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Erreur : " + message, new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12)));
            document.close();
        } catch (DocumentException e) {
            log.error("Erreur lors de la création du PDF d'erreur : {}", e.getMessage());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}