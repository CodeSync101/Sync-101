package tn.missionentreprise.rapportservice.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.missionentreprise.rapportservice.Entity.*;
import tn.missionentreprise.rapportservice.repositories.*;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RapportService {

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

    public ByteArrayInputStream generatePdfRapport(LocalDate startDate, LocalDate endDate, List<String> selectedStudents, List<String> selectedSections) {
        // Fetch all data from repositories
        List<Object[]> commitsByEtudiant = commitRepository.countCommitsByEtudiant();
        List<Object[]> commitsByDate = commitRepository.countCommitsByDate();
        List<PullRequest> pullRequests = pullRequestRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<Object[]> branchesWithUsers = branchRepository.findBranchesWithUserDetails();
        List<Object[]> branchesByDate = branchRepository.countBranchesByDate();
        List<Object[]> ticketsByStatus = ticketRepository.countTicketsByStatus();
        List<Ticket> tickets = ticketRepository.findAll();
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();

        // Apply filters
        commitsByEtudiant = filterCommitsByEtudiant(commitsByEtudiant, selectedStudents);
        commitsByDate = filterCommitsByDate(commitsByDate, startDate, endDate);
        pullRequests = filterPullRequests(pullRequests, startDate, endDate, selectedStudents);
        issues = filterIssues(issues, startDate, endDate, selectedStudents);
        branchesWithUsers = filterBranchesWithUsers(branchesWithUsers, startDate, endDate, selectedStudents);
        branchesByDate = filterBranchesByDate(branchesByDate, startDate, endDate);
        tickets = filterTickets(tickets, startDate, endDate, selectedStudents);
        ticketsByStatus = filterTicketsByStatus(ticketsByStatus, tickets);
        utilisateurs = filterUtilisateurs(utilisateurs, selectedStudents);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA);

            Paragraph title = new Paragraph("Rapport", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));

            // Conditionally add sections based on selectedSections
            if (selectedSections == null || selectedSections.contains("Commits")) {
                addCommitsSection(document, commitsByEtudiant, commitsByDate, headFont, bodyFont);
            }
            if (selectedSections == null || selectedSections.contains("Pull Requests")) {
                addPullRequestsSection(document, pullRequests, headFont, bodyFont);
            }
            if (selectedSections == null || selectedSections.contains("Issues")) {
                addIssuesSection(document, issues, headFont, bodyFont);
            }
            if (selectedSections == null || selectedSections.contains("Branches")) {
                addBranchesSection(document, branchesWithUsers, branchesByDate, headFont, bodyFont);
            }
            if (selectedSections == null || selectedSections.contains("Tickets")) {
                addTicketsSection(document, ticketsByStatus, tickets, headFont, bodyFont);
            }
            if (selectedSections == null || selectedSections.contains("Aide à la prise de décision")) {
                addDecisionSection(document, utilisateurs, commitsByEtudiant, pullRequests, issues, branchesWithUsers, tickets, headFont, bodyFont);
            }

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    // Filter methods
    private List<Object[]> filterCommitsByEtudiant(List<Object[]> commits, List<String> selectedStudents) {
        if (selectedStudents == null || selectedStudents.isEmpty()) return commits;
        return commits.stream()
                .filter(row -> {
                    String fullName = row[0] + " " + row[1];
                    return selectedStudents.contains(fullName);
                })
                .collect(Collectors.toList());
    }

    private List<Object[]> filterCommitsByDate(List<Object[]> commits, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return commits;
        return commits.stream()
                .filter(row -> {
                    LocalDate date = ((Date) row[0]).toLocalDate();
                    return (startDate == null || !date.isBefore(startDate)) && (endDate == null || !date.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }

    private List<PullRequest> filterPullRequests(List<PullRequest> prs, LocalDate startDate, LocalDate endDate, List<String> selectedStudents) {
        if ((startDate == null && endDate == null) && (selectedStudents == null || selectedStudents.isEmpty())) return prs;
        return prs.stream()
                .filter(pr -> {
                    boolean dateMatch = (startDate == null || !pr.getCreatedAt().toLocalDate().isBefore(startDate)) &&
                            (endDate == null || !pr.getCreatedAt().toLocalDate().isAfter(endDate));
                    if (selectedStudents == null || selectedStudents.isEmpty()) return dateMatch;
                    Utilisateur user = utilisateurRepository.findById(pr.getUtilisateurId()).orElse(new Utilisateur());
                    String fullName = (user.getNom() != null ? user.getNom() : "") + " " + (user.getPrenom() != null ? user.getPrenom() : "");
                    return dateMatch && selectedStudents.contains(fullName.trim());
                })
                .collect(Collectors.toList());
    }

    private List<Issue> filterIssues(List<Issue> issues, LocalDate startDate, LocalDate endDate, List<String> selectedStudents) {
        if ((startDate == null && endDate == null) && (selectedStudents == null || selectedStudents.isEmpty())) return issues;
        return issues.stream()
                .filter(issue -> {
                    boolean dateMatch = (startDate == null || !issue.getCreatedAt().toLocalDate().isBefore(startDate)) &&
                            (endDate == null || !issue.getCreatedAt().toLocalDate().isAfter(endDate));
                    if (selectedStudents == null || selectedStudents.isEmpty()) return dateMatch;
                    Utilisateur user = utilisateurRepository.findById(issue.getUtilisateurId()).orElse(new Utilisateur());
                    String fullName = (user.getNom() != null ? user.getNom() : "") + " " + (user.getPrenom() != null ? user.getPrenom() : "");
                    return dateMatch && selectedStudents.contains(fullName.trim());
                })
                .collect(Collectors.toList());
    }

    private List<Object[]> filterBranchesWithUsers(List<Object[]> branches, LocalDate startDate, LocalDate endDate, List<String> selectedStudents) {
        if ((startDate == null && endDate == null) && (selectedStudents == null || selectedStudents.isEmpty())) return branches;
        return branches.stream()
                .filter(row -> {
                    LocalDate date = ((Timestamp) row[3]).toLocalDateTime().toLocalDate();
                    boolean dateMatch = (startDate == null || !date.isBefore(startDate)) && (endDate == null || !date.isAfter(endDate));
                    if (selectedStudents == null || selectedStudents.isEmpty()) return dateMatch;
                    String fullName = row[1] + " " + row[2];
                    return dateMatch && selectedStudents.contains(fullName);
                })
                .collect(Collectors.toList());
    }

    private List<Object[]> filterBranchesByDate(List<Object[]> branches, LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) return branches;
        return branches.stream()
                .filter(row -> {
                    LocalDate date = ((Date) row[0]).toLocalDate();
                    return (startDate == null || !date.isBefore(startDate)) && (endDate == null || !date.isAfter(endDate));
                })
                .collect(Collectors.toList());
    }

    private List<Ticket> filterTickets(List<Ticket> tickets, LocalDate startDate, LocalDate endDate, List<String> selectedStudents) {
        if ((startDate == null && endDate == null) && (selectedStudents == null || selectedStudents.isEmpty())) return tickets;
        return tickets.stream()
                .filter(ticket -> {
                    boolean dateMatch = (startDate == null || !ticket.getLastupdated().toLocalDate().isBefore(startDate)) &&
                            (endDate == null || !ticket.getLastupdated().toLocalDate().isAfter(endDate));
                    if (selectedStudents == null || selectedStudents.isEmpty()) return dateMatch;
                    Utilisateur user = utilisateurRepository.findById(ticket.getAssigneduser_id()).orElse(new Utilisateur());
                    String fullName = (user.getNom() != null ? user.getNom() : "") + " " + (user.getPrenom() != null ? user.getPrenom() : "");
                    return dateMatch && selectedStudents.contains(fullName.trim());
                })
                .collect(Collectors.toList());
    }

    private List<Object[]> filterTicketsByStatus(List<Object[]> ticketsByStatus, List<Ticket> filteredTickets) {
        Map<String, Long> statusCount = filteredTickets.stream()
                .collect(Collectors.groupingBy(Ticket::getStatus, Collectors.counting()));
        return ticketsByStatus.stream()
                .filter(row -> statusCount.containsKey(row[0]))
                .map(row -> new Object[]{row[0], statusCount.getOrDefault(row[0], 0L)})
                .collect(Collectors.toList());
    }

    private List<Utilisateur> filterUtilisateurs(List<Utilisateur> utilisateurs, List<String> selectedStudents) {
        if (selectedStudents == null || selectedStudents.isEmpty()) return utilisateurs;
        return utilisateurs.stream()
                .filter(user -> {
                    String fullName = (user.getNom() != null ? user.getNom() : "") + " " + (user.getPrenom() != null ? user.getPrenom() : "");
                    return selectedStudents.contains(fullName.trim());
                })
                .collect(Collectors.toList());
    }

    // Section methods (unchanged from original, but now use filtered data)
    private void addCommitsSection(Document document, List<Object[]> stats, List<Object[]> commitsByDate, Font headFont, Font bodyFont) throws DocumentException, IOException {
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
            commitsTable.addCell(new PdfPCell(new Phrase((String) row[0], bodyFont)));
            commitsTable.addCell(new PdfPCell(new Phrase((String) row[1], bodyFont)));
            commitsTable.addCell(new PdfPCell(new Phrase(row[2].toString(), bodyFont)));
        }

        document.add(commitsTable);
        document.add(new Paragraph(" "));

        Paragraph chartTitle = new Paragraph("Commits par date", headFont);
        chartTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(chartTitle);
        document.add(new Paragraph(" "));

        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        JFreeChart chart = createCommitsByDateChart(commitsByDate);
        ChartUtils.writeChartAsPNG(chartOut, chart, 500, 300);
        Image chartImage = Image.getInstance(chartOut.toByteArray());
        chartImage.scaleToFit(500, 300);
        chartImage.setAlignment(Element.ALIGN_CENTER);
        document.add(chartImage);
        document.add(new Paragraph(" "));
    }

    private void addPullRequestsSection(Document document, List<PullRequest> pullRequests, Font headFont, Font bodyFont) throws DocumentException, IOException {
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
            Utilisateur utilisateur = utilisateurRepository.findById(pr.getUtilisateurId()).orElse(new Utilisateur());
            String userName = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " + (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            prTable.addCell(new PdfPCell(new Phrase(pr.getTitre() != null ? pr.getTitre() : "", bodyFont)));
            prTable.addCell(new PdfPCell(new Phrase(pr.getCreatedAt() != null ? pr.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
            prTable.addCell(new PdfPCell(new Phrase(pr.getEtat() != null ? pr.getEtat() : "", bodyFont)));
            prTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
        }

        document.add(prTable);
        document.add(new Paragraph(" "));

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

    private void addIssuesSection(Document document, List<Issue> issues, Font headFont, Font bodyFont) throws DocumentException, IOException {
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
            Utilisateur utilisateur = utilisateurRepository.findById(issue.getUtilisateurId()).orElse(new Utilisateur());
            String userName = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " + (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            issueTable.addCell(new PdfPCell(new Phrase(issue.getTitre() != null ? issue.getTitre() : "", bodyFont)));
            issueTable.addCell(new PdfPCell(new Phrase(issue.getContenu() != null ? issue.getContenu() : "", bodyFont)));
            issueTable.addCell(new PdfPCell(new Phrase(issue.getEtat() != null ? issue.getEtat() : "", bodyFont)));
            issueTable.addCell(new PdfPCell(new Phrase(issue.getCreatedAt() != null ? issue.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
            issueTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
        }

        document.add(issueTable);
        document.add(new Paragraph(" "));

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

    private void addBranchesSection(Document document, List<Object[]> branchesWithUsers, List<Object[]> branchesByDate, Font headFont, Font bodyFont) throws DocumentException, IOException {
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
            branchTable.addCell(new PdfPCell(new Phrase((String) row[0], bodyFont)));
            branchTable.addCell(new PdfPCell(new Phrase((String) row[1], bodyFont)));
            branchTable.addCell(new PdfPCell(new Phrase((String) row[2], bodyFont)));
            branchTable.addCell(new PdfPCell(new Phrase(((Timestamp) row[3]).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont)));
        }

        document.add(branchTable);
        document.add(new Paragraph(" "));

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

    private void addTicketsSection(Document document, List<Object[]> ticketsByStatus, List<Ticket> tickets, Font headFont, Font bodyFont) throws DocumentException, IOException {
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
            Utilisateur assignedUser = utilisateurRepository.findById(ticket.getAssigneduser_id()).orElse(new Utilisateur());
            String userName = (assignedUser.getNom() != null ? assignedUser.getNom() : "") + " " + (assignedUser.getPrenom() != null ? assignedUser.getPrenom() : "");
            ticketTable.addCell(new PdfPCell(new Phrase(String.valueOf(ticket.getId()), bodyFont)));
            ticketTable.addCell(new PdfPCell(new Phrase(ticket.getJiraid() != null ? ticket.getJiraid() : "", bodyFont)));
            ticketTable.addCell(new PdfPCell(new Phrase(ticket.getTitle() != null ? ticket.getTitle() : "", bodyFont)));
            ticketTable.addCell(new PdfPCell(new Phrase(ticket.getStatus() != null ? ticket.getStatus() : "", bodyFont)));
            ticketTable.addCell(new PdfPCell(new Phrase(ticket.getLastupdated() != null ? ticket.getLastupdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
            ticketTable.addCell(new PdfPCell(new Phrase(userName.trim(), bodyFont)));
        }

        document.add(ticketTable);
        document.add(new Paragraph(" "));
    }

    private void addDecisionSection(Document document,
                                    List<Utilisateur> utilisateurs,
                                    List<Object[]> stats,
                                    List<PullRequest> pullRequests,
                                    List<Issue> issues,
                                    List<Object[]> branchesWithUsers,
                                    List<Ticket> tickets,
                                    Font headFont,
                                    Font bodyFont) throws DocumentException {
        Paragraph decisionTitle = new Paragraph("Aide à la prise de décision", headFont);
        decisionTitle.setAlignment(Element.ALIGN_LEFT);
        document.add(decisionTitle);
        document.add(new Paragraph(" "));

        PdfPTable decisionTable = new PdfPTable(4);
        decisionTable.setWidthPercentage(100);
        decisionTable.setWidths(new int[]{3, 3, 2, 3});

        Stream.of("Nom", "Prénom", "Score", "Bonus/Malus").forEach(header -> {
            PdfPCell cell = new PdfPCell(new Phrase(header, headFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(Color.LIGHT_GRAY);
            decisionTable.addCell(cell);
        });

        Map<String, Integer> scores = new HashMap<>();
        Map<String, String> bonus = new HashMap<>();

        // Initialiser les scores
        for (Utilisateur utilisateur : utilisateurs) {
            String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                    (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            scores.put(key.trim(), 0);
        }

        // Commits
        for (Object[] row : stats) {
            String key = ((String) row[0] + " " + (String) row[1]).trim();
            int commitCount = ((Long) row[2]).intValue();
            scores.computeIfPresent(key, (k, v) -> v + commitCount * 5);
        }

        // Pull Requests
        for (PullRequest pr : pullRequests) {
            Utilisateur utilisateur = utilisateurRepository.findById(pr.getUtilisateurId()).orElse(new Utilisateur());
            String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                    (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            if ("MERGED".equals(pr.getEtat())) {
                scores.computeIfPresent(key.trim(), (k, v) -> v + 10);
            }
        }

        // Issues
        for (Issue issue : issues) {
            Utilisateur utilisateur = utilisateurRepository.findById(issue.getUtilisateurId()).orElse(new Utilisateur());
            String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                    (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            if ("CLOSED".equals(issue.getEtat())) {
                scores.computeIfPresent(key.trim(), (k, v) -> v + 8);
            }
        }

        // Branches
        for (Object[] row : branchesWithUsers) {
            String key = ((String) row[1] + " " + (String) row[2]).trim();
            scores.computeIfPresent(key, (k, v) -> v + 3);
        }

        // Tickets
        for (Ticket ticket : tickets) {
            Utilisateur utilisateur = utilisateurRepository.findById(ticket.getAssigneduser_id()).orElse(new Utilisateur());
            String key = (utilisateur.getNom() != null ? utilisateur.getNom() : "") + " " +
                    (utilisateur.getPrenom() != null ? utilisateur.getPrenom() : "");
            if ("CLOSED".equals(ticket.getStatus())) {
                scores.computeIfPresent(key.trim(), (k, v) -> v + 7);
            }
        }

        // Bonus / Malus selon score
        scores.forEach((key, score) -> {
            if (score > 50) {
                bonus.put(key, "+2");
            } else if (score > 40) {
                bonus.put(key, "+1.5");
            } else if (score > 30) {
                bonus.put(key, "+1");
            } else if (score > 20) {
                bonus.put(key, "+0.5");
            } else if (score > 10) {
                bonus.put(key, "-1");
            } else if (score > 0) {
                bonus.put(key, "-1.5");
            } else {
                bonus.put(key, "-2");
            }
        });

        // Ajout des lignes au tableau
        scores.forEach((key, score) -> {
            String[] nameParts = key.split(" ", 2);
            String nom = nameParts.length > 0 ? nameParts[0] : "-";
            String prenom = nameParts.length > 1 ? nameParts[1] : "-";
            decisionTable.addCell(new PdfPCell(new Phrase(nom, bodyFont)));
            decisionTable.addCell(new PdfPCell(new Phrase(prenom, bodyFont)));
            decisionTable.addCell(new PdfPCell(new Phrase(String.valueOf(score), bodyFont)));
            decisionTable.addCell(new PdfPCell(new Phrase(bonus.getOrDefault(key, "-"), bodyFont)));
        });

        document.add(decisionTable);
        document.add(new Paragraph(" "));
    }

    // Chart methods (unchanged from original, but now use filtered data)
    private JFreeChart createCommitsByDateChart(List<Object[]> commitsByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] row : commitsByDate) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            Long commitCount = (Long) row[1];
            dataset.addValue(commitCount.intValue(), "Commits", date.toString());
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
        Map<String, Integer> stateCounts = pullRequests.stream()
                .collect(Collectors.groupingBy(PullRequest::getEtat, Collectors.summingInt(e -> 1)));
        stateCounts.forEach((state, count) -> {
            if (count > 0) dataset.setValue(state, count);
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
        return chart;
    }

    private JFreeChart createIssuesByStateChart(List<Issue> issues) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> stateCounts = issues.stream()
                .collect(Collectors.groupingBy(Issue::getEtat, Collectors.summingInt(e -> 1)));
        stateCounts.forEach((state, count) -> {
            if (count > 0) dataset.setValue(state, count);
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
        return chart;
    }

    private JFreeChart createBranchesByDateChart(List<Object[]> branchesByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Object[] row : branchesByDate) {
            LocalDate date = ((Date) row[0]).toLocalDate();
            Long branchCount = (Long) row[1];
            dataset.addValue(branchCount.intValue(), "Branches", date.toString());
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
        for (Object[] row : ticketsByStatus) {
            String status = (String) row[0];
            Long count = (Long) row[1];
            dataset.addValue(count.intValue(), "Tickets", status);
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