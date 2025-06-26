package com.mission_entreprise.web_api.repositories.report;

import com.mission_entreprise.web_api.entities.report.BranchReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchReportRepository extends JpaRepository<BranchReport, Long> {

    @Query(value = "SELECT b.name, u.nom, u.prenom, c.date_commit " +
            "FROM branch_report b " +
            "JOIN commit_report c ON b.commit_id = c.id " +
            "JOIN utilisateur u ON c.utilisateur_id = u.id", nativeQuery = true)
    List<Object[]> findBranchesWithUserDetails();

    @Query(value = "SELECT DATE(c.date_commit) as creation_date, COUNT(b.id) as branch_count " +
            "FROM branch_report b " +
            "JOIN commit_report c ON b.commit_id = c.id " +
            "GROUP BY DATE(c.date_commit) " +
            "ORDER BY DATE(c.date_commit)", nativeQuery = true)
    List<Object[]> countBranchesByDate();

}
