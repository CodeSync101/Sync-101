package com.mission_entreprise.web_api.repositories.report;

import com.mission_entreprise.web_api.entities.report.PullRequestReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullRequestReportRepository extends JpaRepository<PullRequestReport, Long> {
}
