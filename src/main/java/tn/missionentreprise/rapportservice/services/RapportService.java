package tn.missionentreprise.rapportservice.services;

import com.lowagie.text.*;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.missionentreprise.rapportservice.Entity.*;
import tn.missionentreprise.rapportservice.repositories.BranchRepository;
import tn.missionentreprise.rapportservice.repositories.CommitRepository;
import tn.missionentreprise.rapportservice.repositories.IssueRepository;
import tn.missionentreprise.rapportservice.repositories.PullRequestRepository;
import tn.missionentreprise.rapportservice.repositories.TicketRepository;
import tn.missionentreprise.rapportservice.repositories.UtilisateurRepository;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

@Service
public class RapportService {

    private static final Logger log = LoggerFactory.getLogger(RapportService.class);

    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private PullRequestRepository pullRequestRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private TicketRepository ticketRepository;

    public ByteArrayInputStream generatePdfRapport(Timestamp startDate, Timestamp endDate, List<Long> userIds, Set<String> selectedSections) {
        // Validation des paramètres
        boolean useAllUsers = (userIds == null || userIds.isEmpty());
        List<Long> safeUserIds = useAllUsers ? utilisateurRepository.findAll()
                .stream().map(Utilisateur::getId).toList() : userIds;

        if (useAllUsers) {
            log.warn("Aucun utilisateur fourni pour la génération du rapport. Utilisation de tous les utilisateurs.");
        }

        Timestamp safeStartDate = (startDate != null) ? startDate : Timestamp.valueOf("1970-01-01 00:00:00");
        Timestamp safeEndDate = (endDate != null) ? endDate : Timestamp.valueOf("9999-12-31 23:59:59");

        log.info("Génération des données pour le rapport - startDate: {}, endDate: {}, userIds: {}, sections: {}",
                safeStartDate, safeEndDate, safeUserIds, selectedSections);

// Récupération des données avec filtres
        List<Object[]> stats = List.of();
        List<Object[]> commitsByDate = List.of();
        List<PullRequest> pullRequests = List.of();
        List<Issue> issues = List.of();
        List<Object[]> branchesWithUsers = List.of();
        List<Object[]> branchesByDate = List.of();
        List<Object[]> ticketsByStatus = List.of();
        List<Ticket> tickets = List.of();

        try {
            if (selectedSections.contains("Commits")) {
                stats = commitRepository.countCommitsByEtudiant(safeStartDate, safeEndDate, safeUserIds);
                commitsByDate = commitRepository.countCommitsByDate(safeStartDate, safeEndDate, safeUserIds);
                log.info("Commits par étudiant: {}", stats);
                log.info("Commits par date: {}", commitsByDate);
            }

            LocalDateTime startDateTime = safeStartDate.toLocalDateTime();
            LocalDateTime endDateTime = safeEndDate.toLocalDateTime();

            if (selectedSections.contains("Pull Requests")) {
                pullRequests = pullRequestRepository.findByFilters(startDateTime, endDateTime, safeUserIds);
                log.info("Pull Requests: {}", pullRequests);
            }

            if (selectedSections.contains("Issues")) {
                issues = issueRepository.findAll();
                log.info("Issues: {}", issues);
            }

            List<Object[]> branches = Collections.emptyList();



            if (selectedSections.contains("Branches")) {
                try {
                    branches = branchRepository.findBranchesWithUserDetailsNative(
                            startDateTime != null ? Timestamp.valueOf(startDateTime) : null,
                            endDateTime != null ? Timestamp.valueOf(endDateTime) : null,
                            safeUserIds
                    );

                    for (Object[] row : branches) {
                        String branchName = (String) row[0];
                        String userNom = (String) row[1];
                        String userPrenom = (String) row[2];
                        Timestamp commitDate = (Timestamp) row[3];

                        log.info("Branch: {}, Nom: {}, Prénom: {}, Date: {}", branchName, userNom, userPrenom, commitDate);
                    }

                } catch (Exception e) {
                    log.error("Erreur lors de la récupération des branches", e);
                    throw new RuntimeException("Erreur lors de la récupération des branches", e);
                }
            }


            if (selectedSections.contains("Tickets")) {
                // Conversion List<Long> -> Long[]
                Long[] userIdsArray = safeUserIds.toArray(new Long[0]);

                ticketsByStatus = ticketRepository.countTicketsByStatus(startDateTime, endDateTime, userIdsArray);
                tickets = ticketRepository.findByFilters(startDateTime, endDateTime, safeUserIds);

                log.info("Tickets par statut: {}", ticketsByStatus);
                log.info("Tickets: {}", tickets);
            }


        } catch (Exception e) {
            log.error("Erreur lors de la récupération des données des repositories", e);
            throw new RuntimeException("Erreur lors de la récupération des données pour le rapport", e);
        }

        List<Utilisateur> utilisateurs = selectedSections.contains("Aide à la prise de décision") ?
                utilisateurRepository.findAllById(safeUserIds) : utilisateurRepository.findAll();
        log.info("Utilisateurs: {}", utilisateurs);


        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            com.lowagie.text.Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            com.lowagie.text.Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA);

            Paragraph title = new Paragraph("Rapport", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Section 1: Commits par étudiant
            if (selectedSections.contains("Commits") && !stats.isEmpty()) {
                Paragraph commitsTitle = new Paragraph("Commits par étudiant", headFont);
                commitsTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(commitsTitle);
                document.add(new Paragraph(" "));

                PdfPTable commitsTable = new PdfPTable(3);
                commitsTable.setWidthPercentage(100);
                commitsTable.setWidths(new int[]{4, 4, 2});

                Stream.of("Nom", "Prénom", "Commits").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    commitsTable.addCell(cell);
                });

                for (Object[] row : stats) {
                    commitsTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[0]), bodyFont)));
                    commitsTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[1]), bodyFont)));
                    commitsTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[2]), bodyFont)));
                }

                document.add(commitsTable);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Commits")) {
                document.add(new Paragraph("Aucune donnée pour les commits par étudiant.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 2: Commits par date
            if (selectedSections.contains("Commits") && !commitsByDate.isEmpty()) {
                Paragraph chartTitle = new Paragraph("Commits par date", headFont);
                chartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(chartTitle);
                document.add(new Paragraph(" "));

                ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
                JFreeChart chart = createCommitsByDateChart(commitsByDate);
                int width = 500;
                int height = 300;
                ChartUtils.writeChartAsPNG(chartOut, chart, width, height);
                Image chartImage = Image.getInstance(chartOut.toByteArray());
                chartImage.scaleToFit(500, 300);
                chartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(chartImage);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Commits")) {
                document.add(new Paragraph("Aucune donnée pour les commits par date.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 3: Liste des Pull Requests
            if (selectedSections.contains("Pull Requests") && !pullRequests.isEmpty()) {
                Paragraph prTitle = new Paragraph("Liste des Pull Requests", headFont);
                prTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(prTitle);
                document.add(new Paragraph(" "));

                PdfPTable prTable = new PdfPTable(4);
                prTable.setWidthPercentage(100);
                prTable.setWidths(new int[]{4, 3, 2, 3});

                Stream.of("Titre", "Date de création", "État", "Utilisateur").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    prTable.addCell(cell);
                });

                for (PullRequest pr : pullRequests) {
                    Utilisateur utilisateur = utilisateurRepository.findById(pr.getUtilisateurId())
                            .orElse(new Utilisateur());
                    String userName = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    prTable.addCell(new PdfPCell(new Phrase(pr.getTitre() != null ? pr.getTitre() : "", bodyFont)));
                    prTable.addCell(new PdfPCell(new Phrase(pr.getCreatedAt() != null ? pr.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
                    prTable.addCell(new PdfPCell(new Phrase(pr.getEtat() != null ? pr.getEtat() : "", bodyFont)));
                    prTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
                }

                document.add(prTable);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Pull Requests")) {
                document.add(new Paragraph("Aucune donnée pour les Pull Requests.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 4: Répartition des Pull Requests par État
            if (selectedSections.contains("Pull Requests") && !pullRequests.isEmpty()) {
                Paragraph prChartTitle = new Paragraph("Répartition des Pull Requests par État", headFont);
                prChartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(prChartTitle);
                document.add(new Paragraph(" "));

                ByteArrayOutputStream prChartOut = new ByteArrayOutputStream();
                JFreeChart prChart = createPullRequestsByStateChart(pullRequests);
                ChartUtils.writeChartAsPNG(prChartOut, prChart, 500, 300);
                Image prChartImage = Image.getInstance(prChartOut.toByteArray());
                prChartImage.scaleToFit(500, 300);
                prChartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(prChartImage);
                document.add(new Paragraph(" "));
            }

            // Section 5: Liste des Issues
            if (selectedSections.contains("Issues") && !issues.isEmpty()) {
                Paragraph issueTitle = new Paragraph("Liste des Issues", headFont);
                issueTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(issueTitle);
                document.add(new Paragraph(" "));

                PdfPTable issueTable = new PdfPTable(5);
                issueTable.setWidthPercentage(100);
                issueTable.setWidths(new int[]{3, 4, 2, 3, 3});

                Stream.of("Titre", "Contenu", "État", "Date de création", "Utilisateur").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    issueTable.addCell(cell);
                });

                for (Issue issue : issues) {
                    Utilisateur utilisateur = utilisateurRepository.findById(issue.getUtilisateurId())
                            .orElse(new Utilisateur());
                    String userName = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    issueTable.addCell(new PdfPCell(new Phrase(issue.getTitre() != null ? issue.getTitre() : "", bodyFont)));
                    issueTable.addCell(new PdfPCell(new Phrase(issue.getContenu() != null ? issue.getContenu() : "", bodyFont)));
                    issueTable.addCell(new PdfPCell(new Phrase(issue.getEtat() != null ? issue.getEtat() : "", bodyFont)));
                    issueTable.addCell(new PdfPCell(new Phrase(issue.getCreatedAt() != null ? issue.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
                    issueTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
                }

                document.add(issueTable);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Issues")) {
                document.add(new Paragraph("Aucune donnée pour les Issues.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 6: Répartition des Issues par État
            if (selectedSections.contains("Issues") && !issues.isEmpty()) {
                Paragraph issueChartTitle = new Paragraph("Répartition des Issues par État", headFont);
                issueChartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(issueChartTitle);
                document.add(new Paragraph(" "));

                ByteArrayOutputStream issueChartOut = new ByteArrayOutputStream();
                JFreeChart issueChart = createIssuesByStateChart(issues);
                ChartUtils.writeChartAsPNG(issueChartOut, issueChart, 500, 300);
                Image issueChartImage = Image.getInstance(issueChartOut.toByteArray());
                issueChartImage.scaleToFit(500, 300);
                issueChartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(issueChartImage);
                document.add(new Paragraph(" "));
            }

            // Section 7: Liste des Branches par Étudiant
            if (selectedSections.contains("Branches") && !branchesWithUsers.isEmpty()) {
                Paragraph branchTitle = new Paragraph("Liste des Branches par Étudiant", headFont);
                branchTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(branchTitle);
                document.add(new Paragraph(" "));

                PdfPTable branchTable = new PdfPTable(4);
                branchTable.setWidthPercentage(100);
                branchTable.setWidths(new int[]{4, 3, 3, 3});

                Stream.of("Nom de la Branche", "Nom", "Prénom", "Date de Création").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    branchTable.addCell(cell);
                });

                for (Object[] row : branchesWithUsers) {
                    branchTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[0]), bodyFont)));
                    branchTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[1]), bodyFont)));
                    branchTable.addCell(new PdfPCell(new Phrase(String.valueOf(row[2]), bodyFont)));
                    branchTable.addCell(new PdfPCell(new Phrase(((Timestamp) row[3]).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont)));
                }

                document.add(branchTable);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Branches")) {
                document.add(new Paragraph("Aucune donnée pour les Branches.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 8: Branches Créées par Date
            if (selectedSections.contains("Branches") && !branchesByDate.isEmpty()) {
                Paragraph branchChartTitle = new Paragraph("Branches Créées par Date", headFont);
                branchChartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(branchChartTitle);
                document.add(new Paragraph(" "));

                ByteArrayOutputStream branchChartOut = new ByteArrayOutputStream();
                JFreeChart branchChart = createBranchesByDateChart(branchesByDate);
                ChartUtils.writeChartAsPNG(branchChartOut, branchChart, 500, 300);
                Image branchChartImage = Image.getInstance(branchChartOut.toByteArray());
                branchChartImage.scaleToFit(500, 300);
                branchChartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(branchChartImage);
                document.add(new Paragraph(" "));
            }

            // Section 9: Tickets par Statut
            if (selectedSections.contains("Tickets") && !ticketsByStatus.isEmpty()) {
                Paragraph ticketChartTitle = new Paragraph("Tickets par Statut", headFont);
                ticketChartTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(ticketChartTitle);
                document.add(new Paragraph(" "));

                ByteArrayOutputStream ticketChartOut = new ByteArrayOutputStream();
                JFreeChart ticketChart = createTicketsByStatusChart(ticketsByStatus);
                ChartUtils.writeChartAsPNG(ticketChartOut, ticketChart, 500, 300);
                Image ticketChartImage = Image.getInstance(ticketChartOut.toByteArray());
                ticketChartImage.scaleToFit(500, 300);
                ticketChartImage.setAlignment(Element.ALIGN_CENTER);
                document.add(ticketChartImage);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Tickets")) {
                document.add(new Paragraph("Aucune donnée pour les Tickets par statut.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 10: Liste des Tickets
            if (selectedSections.contains("Tickets") && !tickets.isEmpty()) {
                Paragraph ticketListTitle = new Paragraph("Liste des Tickets", headFont);
                ticketListTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(ticketListTitle);
                document.add(new Paragraph(" "));

                PdfPTable ticketTable = new PdfPTable(6);
                ticketTable.setWidthPercentage(100);
                ticketTable.setWidths(new int[]{2, 3, 4, 2, 3, 3});

                Stream.of("ID", "Jira ID", "Titre", "Statut", "Dernière Mise à Jour", "Utilisateur Assigné").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    ticketTable.addCell(cell);
                });

                for (Ticket ticket : tickets) {
                    Utilisateur assignedUser = utilisateurRepository.findById(ticket.getAssigneduser_id())
                            .orElse(new Utilisateur());
                    String userName = (assignedUser.getNom() != null ? assignedUser.getNom() : "") + " " +
                            (assignedUser.getPrenom() != null ? assignedUser.getPrenom() : "");
                    ticketTable.addCell(new PdfPCell(new Phrase(String.valueOf(ticket.getId()), bodyFont)));
                    ticketTable.addCell(new PdfPCell(new Phrase(ticket.getJiraid() != null ? ticket.getJiraid() : "", bodyFont)));
                    ticketTable.addCell(new PdfPCell(new Phrase(ticket.getTitle() != null ? ticket.getTitle() : "", bodyFont)));
                    ticketTable.addCell(new PdfPCell(new Phrase(ticket.getStatus() != null ? ticket.getStatus() : "", bodyFont)));
                    ticketTable.addCell(new PdfPCell(new Phrase(ticket.getLastupdated() != null ? ticket.getLastupdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
                    ticketTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
                }

                document.add(ticketTable);
                document.add(new Paragraph(" "));
            } else if (selectedSections.contains("Tickets")) {
                document.add(new Paragraph("Aucune donnée pour la liste des Tickets.", bodyFont));
                document.add(new Paragraph(" "));
            }

            // Section 11: Aide à la prise de décision
            if (selectedSections.contains("Aide à la prise de décision")) {
                Paragraph decisionTitle = new Paragraph("Aide à la prise de décision", headFont);
                decisionTitle.setAlignment(Element.ALIGN_LEFT);
                document.add(decisionTitle);
                document.add(new Paragraph(" "));

                PdfPTable decisionTable = new PdfPTable(5);
                decisionTable.setWidthPercentage(100);
                decisionTable.setWidths(new int[]{3, 3, 2, 3, 3});

                Stream.of("Nom", "Prénom", "Score", "Bonus", "Malus").forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(Color.LIGHT_GRAY);
                    decisionTable.addCell(cell);
                });

                // Calcul des scores, bonus et malus
                Map<String, Integer> scores = new HashMap<>();
                Map<String, String> bonus = new HashMap<>();
                Map<String, String> malus = new HashMap<>();

                // Initialisation des scores et malus
                for (Utilisateur utilisateur : utilisateurs) {
                    String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    scores.put(key.trim(), 0);
                    malus.put(key.trim(), "-");
                    bonus.put(key.trim(), "-");
                }

                // Score des commits
                for (Object[] row : stats) {
                    String key = String.valueOf(row[0]) + " " + String.valueOf(row[1]);
                    int commitCount = ((Long) row[2]).intValue();
                    scores.compute(key.trim(), (k, v) -> v + (commitCount * 5));
                }

                // Score des pull requests
                for (PullRequest pr : pullRequests) {
                    Utilisateur utilisateur = utilisateurRepository.findById(pr.getUtilisateurId()).orElse(new Utilisateur());
                    String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    if ("MERGED".equals(pr.getEtat())) {
                        scores.compute(key.trim(), (k, v) -> v + 10);
                    }
                }

                // Score des issues
                for (Issue issue : issues) {
                    Utilisateur utilisateur = utilisateurRepository.findById(issue.getUtilisateurId()).orElse(new Utilisateur());
                    String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    if ("CLOSED".equals(issue.getEtat())) {
                        scores.compute(key.trim(), (k, v) -> v + 8);
                    } else if ("OPEN".equals(issue.getEtat()) && issue.getCreatedAt() != null) {
                        long daysOpen = ChronoUnit.DAYS.between(issue.getCreatedAt(), LocalDateTime.now());
                        if (daysOpen > 30) {
                            malus.put(key.trim(), "À surveiller (Issue ouverte > 30 jours)");
                        }
                    }
                }

                // Score des branches
                for (Object[] row : branchesWithUsers) {
                    String key = String.valueOf(row[1]) + " " + String.valueOf(row[2]);
                    scores.compute(key.trim(), (k, v) -> v + 3);
                }

                // Score des tickets
                for (Ticket ticket : tickets) {
                    Utilisateur utilisateur = utilisateurRepository.findById(ticket.getAssigneduser_id()).orElse(new Utilisateur());
                    String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                            (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
                    if ("CLOSED".equals(ticket.getStatus())) {
                        scores.compute(key.trim(), (k, v) -> v + 7);
                    } else if ("OPEN".equals(ticket.getStatus()) && ticket.getLastupdated() != null) {
                        long daysOpen = ChronoUnit.DAYS.between(ticket.getLastupdated(), LocalDateTime.now());
                        if (daysOpen > 30) {
                            malus.compute(key.trim(), (k, v) -> v.equals("-") ? "À surveiller (Ticket ouvert > 30 jours)" : v);
                        }
                    }
                }

                // Attribution des bonus
                scores.forEach((key, score) -> {
                    bonus.put(key, score > 50 ? "+2" : "-");
                });

                // Ajout des lignes au tableau
                scores.forEach((key, score) -> {
                    String[] nameParts = key.split(" ", 2);
                    decisionTable.addCell(new PdfPCell(new Phrase(nameParts[0], bodyFont)));
                    decisionTable.addCell(new PdfPCell(new Phrase(nameParts.length > 1 ? nameParts[1] : "", bodyFont)));
                    decisionTable.addCell(new PdfPCell(new Phrase(String.valueOf(score), bodyFont)));
                    decisionTable.addCell(new PdfPCell(new Phrase(bonus.getOrDefault(key, "-"), bodyFont)));
                    decisionTable.addCell(new PdfPCell(new Phrase(malus.getOrDefault(key, "-"), bodyFont)));
                });

                document.add(decisionTable);
            } else {
                document.add(new Paragraph("Aucune donnée pour l'aide à la prise de décision.", bodyFont));
            }

            document.close();
        } catch (DocumentException | IOException e) {
            log.error("Erreur lors de la génération du PDF", e);
            throw new RuntimeException("Erreur lors de la génération du PDF : " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private JFreeChart createCommitsByDateChart(List<Object[]> commitsByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (commitsByDate == null || commitsByDate.isEmpty()) {
            log.warn("Aucune donnée pour les commits par date.");
        } else {
            for (Object[] row : commitsByDate) {
                try {
                    Timestamp timestamp = (Timestamp) row[0];
                    LocalDateTime dateTime = timestamp.toLocalDateTime();
                    Long commitCount = (Long) row[1];
                    dataset.addValue(commitCount.intValue(), "Commits", dateTime.toLocalDate().toString());
                } catch (ClassCastException e) {
                    log.error("Erreur lors de la conversion des données pour les commits par date", e);
                }
            }
        }

        return ChartFactory.createLineChart(
                "Nombre de commits par date",
                "Date",
                "Nombre de commits",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private JFreeChart createPullRequestsByStateChart(List<PullRequest> pullRequests) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Integer> stateCounts = new HashMap<>();
        stateCounts.put("OPEN", 0);
        stateCounts.put("CLOSED", 0);
        stateCounts.put("MERGED", 0);

        for (PullRequest pr : pullRequests) {
            String state = pr.getEtat();
            stateCounts.put(state, stateCounts.getOrDefault(state, 0) + 1);
        }

        stateCounts.forEach((state, count) -> {
            if (count > 0) {
                dataset.setValue(state, count);
            }
        });

        JFreeChart chart = ChartFactory.createPieChart(
                "Répartition des Pull Requests par État",
                dataset,
                true, true, false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("OPEN", new Color(0, 128, 0));
        plot.setSectionPaint("CLOSED", new Color(255, 99, 71));
        plot.setSectionPaint("MERGED", new Color(65, 105, 225));
        plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        plot.setLabelBackgroundPaint(new Color(240, 240, 240));
        plot.setLabelOutlinePaint(Color.BLACK);
        plot.setLabelShadowPaint(Color.GRAY);
        plot.setShadowXOffset(5.0f);
        plot.setShadowYOffset(5.0f);
        plot.setBackgroundPaint(new Color(255, 250, 240));
        plot.setOutlinePaint(Color.DARK_GRAY);
        plot.setOutlineStroke(new BasicStroke(1.5f));
        plot.setSimpleLabels(true);

        return chart;
    }

    private JFreeChart createIssuesByStateChart(List<Issue> issues) {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<String, Integer> stateCounts = new HashMap<>();
        stateCounts.put("OPEN", 0);
        stateCounts.put("CLOSED", 0);
        stateCounts.put("MERGED", 0);

        for (Issue issue : issues) {
            String state = issue.getEtat();
            stateCounts.put(state, stateCounts.getOrDefault(state, 0) + 1);
        }

        stateCounts.forEach((state, count) -> {
            if (count > 0) {
                dataset.setValue(state, count);
            }
        });

        JFreeChart chart = ChartFactory.createPieChart(
                "Répartition des Issues par État",
                dataset,
                true, true, false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setSectionPaint("OPEN", new Color(0, 128, 0));
        plot.setSectionPaint("CLOSED", new Color(255, 99, 71));
        plot.setSectionPaint("MERGED", new Color(65, 105, 225));
        plot.setLabelFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, 12));
        plot.setLabelBackgroundPaint(new Color(240, 240, 240));
        plot.setLabelOutlinePaint(Color.BLACK);
        plot.setLabelShadowPaint(Color.GRAY);
        plot.setShadowXOffset(5.0f);
        plot.setShadowYOffset(5.0f);
        plot.setBackgroundPaint(new Color(255, 250, 240));
        plot.setOutlinePaint(Color.DARK_GRAY);
        plot.setOutlineStroke(new BasicStroke(1.5f));
        plot.setSimpleLabels(true);

        return chart;
    }

    private JFreeChart createBranchesByDateChart(List<Object[]> branchesByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (branchesByDate == null || branchesByDate.isEmpty()) {
            log.warn("Aucune donnée pour les branches par date.");
        } else {
            for (Object[] row : branchesByDate) {
                try {
                    Timestamp timestamp = (Timestamp) row[0];
                    LocalDateTime dateTime = timestamp.toLocalDateTime();
                    Long branchCount = (Long) row[1];
                    dataset.addValue(branchCount.intValue(), "Branches", dateTime.toLocalDate().toString());
                } catch (ClassCastException e) {
                    log.error("Erreur lors de la conversion des données pour les branches par date", e);
                }
            }
        }

        return ChartFactory.createLineChart(
                "Nombre de branches créées par date",
                "Date",
                "Nombre de branches",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }

    private JFreeChart createTicketsByStatusChart(List<Object[]> ticketsByStatus) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        if (ticketsByStatus == null || ticketsByStatus.isEmpty()) {
            log.warn("Aucune donnée pour les tickets par statut.");
        } else {
            for (Object[] row : ticketsByStatus) {
                try {
                    String status = String.valueOf(row[0]);
                    Long count = (Long) row[1];
                    dataset.addValue(count.intValue(), "Tickets", status);
                } catch (ClassCastException e) {
                    log.error("Erreur lors de la conversion des données pour les tickets par statut", e);
                }
            }
        }

        return ChartFactory.createBarChart(
                "Nombre de Tickets par Statut",
                "Statut",
                "Nombre de Tickets",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
    }
}