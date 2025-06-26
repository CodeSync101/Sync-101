package com.mission_entreprise.web_api.repositories.report;

import com.mission_entreprise.web_api.entities.report.IssueReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IssueReportRepository extends JpaRepository<IssueReport, Long> {
}
