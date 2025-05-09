package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.PullRequest;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {
}