package com.mission_entreprise.web_api.repositories.report;

import com.mission_entreprise.web_api.entities.report.CommitReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitReportRepository extends JpaRepository<CommitReport, Long> {

    @Query("SELECT u.nom, u.prenom, COUNT(c.id) " +
            "FROM CommitReport c JOIN c.utilisateur u " +
            "GROUP BY u.nom, u.prenom")
    List<Object[]> countCommitsByEtudiant();

    @Query(value = "SELECT CAST(c.date_commit AS DATE), COUNT(c.id) " +
            "FROM commit_report c " +
            "GROUP BY CAST(c.date_commit AS DATE) " +
            "ORDER BY CAST(c.date_commit AS DATE)", nativeQuery = true)
    List<Object[]> countCommitsByDate();
}
