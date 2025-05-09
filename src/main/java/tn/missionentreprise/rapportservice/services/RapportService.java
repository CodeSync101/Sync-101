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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.missionentreprise.rapportservice.Entity.Issue;
import tn.missionentreprise.rapportservice.Entity.PullRequest;
import tn.missionentreprise.rapportservice.Entity.Ticket;
import tn.missionentreprise.rapportservice.Entity.Utilisateur;
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
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public ByteArrayInputStream generatePdfRapport() {
        List<Object[]> stats = commitRepository.countCommitsByEtudiant();
        List<Object[]> commitsByDate = commitRepository.countCommitsByDate();
        List<PullRequest> pullRequests = pullRequestRepository.findAll();
        List<Issue> issues = issueRepository.findAll();
        List<Object[]> branchesWithUsers = branchRepository.findBranchesWithUserDetails();
        List<Object[]> branchesByDate = branchRepository.countBranchesByDate();
        List<Object[]> ticketsByStatus = ticketRepository.countTicketsByStatus();
        List<Ticket> tickets = ticketRepository.findAll();

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

            // Section 1
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

            // Section 2
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

            // Section 3
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
                prTable.addCell(new PdfPCell(new Phrase(pr.getTitre(), bodyFont)));
                prTable.addCell(new PdfPCell(new Phrase(pr.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont)));
                prTable.addCell(new PdfPCell(new Phrase(pr.getEtat(), bodyFont)));
                prTable.addCell(new PdfPCell(new Phrase(utilisateur.getNom() + " " + utilisateur.getPrenom(), bodyFont)));
            }

            document.add(prTable);
            document.add(new Paragraph(" "));

            // Section 4
            Paragraph prChartTitle = new Paragraph("Répartition des Pull Requests par État", headFont);
            prChartTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(prChartTitle);
            document.add(new Paragraph(" "));

            ByteArrayOutputStream prChartOut = new ByteArrayOutputStream();
            JFreeChart prChart = createPullRequestsByStateChart(pullRequests);
            ChartUtils.writeChartAsPNG(prChartOut, prChart, width, height);
            Image prChartImage = Image.getInstance(prChartOut.toByteArray());
            prChartImage.scaleToFit(500, 300);
            prChartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(prChartImage);
            document.add(new Paragraph(" "));

            // Section 5
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
                issueTable.addCell(new PdfPCell(new Phrase(issue.getTitre(), bodyFont)));
                issueTable.addCell(new PdfPCell(new Phrase(issue.getContenu(), bodyFont)));
                issueTable.addCell(new PdfPCell(new Phrase(issue.getEtat(), bodyFont)));
                issueTable.addCell(new PdfPCell(new Phrase(issue.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont)));
                issueTable.addCell(new PdfPCell(new Phrase(utilisateur.getNom() + " " + utilisateur.getPrenom(), bodyFont)));
            }

            document.add(issueTable);
            document.add(new Paragraph(" "));

            // Section 6
            Paragraph issueChartTitle = new Paragraph("Répartition des Issues par État", headFont);
            issueChartTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(issueChartTitle);
            document.add(new Paragraph(" "));

            ByteArrayOutputStream issueChartOut = new ByteArrayOutputStream();
            JFreeChart issueChart = createIssuesByStateChart(issues);
            ChartUtils.writeChartAsPNG(issueChartOut, issueChart, width, height);
            Image issueChartImage = Image.getInstance(issueChartOut.toByteArray());
            issueChartImage.scaleToFit(500, 300);
            issueChartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(issueChartImage);
            document.add(new Paragraph(" "));

            // Section 7
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
                branchTable.addCell(new PdfPCell(new Phrase((String) row[0], bodyFont))); // Nom de la branche
                branchTable.addCell(new PdfPCell(new Phrase((String) row[1], bodyFont))); // Nom de l'étudiant
                branchTable.addCell(new PdfPCell(new Phrase((String) row[2], bodyFont))); // Prénom de l'étudiant
                branchTable.addCell(new PdfPCell(new Phrase(((Timestamp) row[3]).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), bodyFont))); // Date de création
            }

            document.add(branchTable);
            document.add(new Paragraph(" "));

            // Section 8
            Paragraph branchChartTitle = new Paragraph("Branches Créées par Date", headFont);
            branchChartTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(branchChartTitle);
            document.add(new Paragraph(" "));

            ByteArrayOutputStream branchChartOut = new ByteArrayOutputStream();
            JFreeChart branchChart = createBranchesByDateChart(branchesByDate);
            ChartUtils.writeChartAsPNG(branchChartOut, branchChart, width, height);
            Image branchChartImage = Image.getInstance(branchChartOut.toByteArray());
            branchChartImage.scaleToFit(500, 300);
            branchChartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(branchChartImage);
            document.add(new Paragraph(" "));

            // Section 9
            Paragraph ticketChartTitle = new Paragraph("Tickets par Statut", headFont);
            ticketChartTitle.setAlignment(Element.ALIGN_LEFT);
            document.add(ticketChartTitle);
            document.add(new Paragraph(" "));

            ByteArrayOutputStream ticketChartOut = new ByteArrayOutputStream();
            JFreeChart ticketChart = createTicketsByStatusChart(ticketsByStatus);
            ChartUtils.writeChartAsPNG(ticketChartOut, ticketChart, width, height);
            Image ticketChartImage = Image.getInstance(ticketChartOut.toByteArray());
            ticketChartImage.scaleToFit(500, 300);
            ticketChartImage.setAlignment(Element.ALIGN_CENTER);
            document.add(ticketChartImage);
            document.add(new Paragraph(" "));

            // Section 10 : Liste des ticket
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
                ticketTable.addCell(new PdfPCell(new Phrase(String.valueOf(ticket.getId()), bodyFont)));
                ticketTable.addCell(new PdfPCell(new Phrase(ticket.getJiraid() != null ? ticket.getJiraid() : "", bodyFont)));
                ticketTable.addCell(new PdfPCell(new Phrase(ticket.getTitle() != null ? ticket.getTitle() : "", bodyFont)));
                ticketTable.addCell(new PdfPCell(new Phrase(ticket.getStatus() != null ? ticket.getStatus() : "", bodyFont)));
                ticketTable.addCell(new PdfPCell(new Phrase(ticket.getLastupdated() != null ? ticket.getLastupdated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "", bodyFont)));
                ticketTable.addCell(new PdfPCell(new Phrase(assignedUser.getNom() + " " + assignedUser.getPrenom(), bodyFont)));
            }

            document.add(ticketTable);

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private JFreeChart createCommitsByDateChart(List<Object[]> commitsByDate) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Commits by date raw: " + commitsByDate);
        if (commitsByDate == null || commitsByDate.isEmpty()) {
            System.out.println("No data found for commits by date.");
        } else {
            for (Object[] row : commitsByDate) {
                System.out.println("Row[0] type: " + row[0].getClass().getName() + ", value: " + row[0]);
                System.out.println("Row[1] type: " + row[1].getClass().getName() + ", value: " + row[1]);

                try {
                    Date sqlDate = (Date) row[0];
                    LocalDate date = sqlDate.toLocalDate();
                    Long commitCount = (Long) row[1];
                    dataset.addValue(commitCount.intValue(), "Commits", date.toString());
                    System.out.println("Processed - Date: " + date + ", Commits: " + commitCount);
                } catch (ClassCastException e) {
                    System.err.println("Error casting row[0] to Date: " + e.getMessage());
                }
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Nombre de commits par date",
                "Date",
                "Nombre de commits",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
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

        System.out.println("Branches by date raw: " + branchesByDate);
        if (branchesByDate == null || branchesByDate.isEmpty()) {
            System.out.println("No data found for branches by date.");
        } else {
            for (Object[] row : branchesByDate) {
                System.out.println("Row[0] type: " + row[0].getClass().getName() + ", value: " + row[0]);
                System.out.println("Row[1] type: " + row[1].getClass().getName() + ", value: " + row[1]);

                try {
                    Date sqlDate = (Date) row[0];
                    LocalDate date = sqlDate.toLocalDate();
                    Long branchCount = (Long) row[1];
                    dataset.addValue(branchCount.intValue(), "Branches", date.toString());
                    System.out.println("Processed - Date: " + date + ", Branches: " + branchCount);
                } catch (ClassCastException e) {
                    System.err.println("Error casting row[0] to Date: " + e.getMessage());
                }
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Nombre de branches créées par date",
                "Date",
                "Nombre de branches",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }

    private JFreeChart createTicketsByStatusChart(List<Object[]> ticketsByStatus) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        System.out.println("Tickets by status raw: " + ticketsByStatus);
        if (ticketsByStatus == null || ticketsByStatus.isEmpty()) {
            System.out.println("No data found for tickets by status.");
        } else {
            for (Object[] row : ticketsByStatus) {
                System.out.println("Row[0] type: " + row[0].getClass().getName() + ", value: " + row[0]);
                System.out.println("Row[1] type: " + row[1].getClass().getName() + ", value: " + row[1]);
                try {
                    String status = (String) row[0];
                    Long count = (Long) row[1];
                    dataset.addValue(count.intValue(), "Tickets", status);
                    System.out.println("Processed - Status: " + status + ", Count: " + count);
                } catch (ClassCastException e) {
                    System.err.println("Error casting row values: " + e.getMessage());
                }
            }
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Nombre de Tickets par Statut",
                "Statut",
                "Nombre de Tickets",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        return chart;
    }
}