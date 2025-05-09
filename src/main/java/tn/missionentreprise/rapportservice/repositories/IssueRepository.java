package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Issue;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {
}