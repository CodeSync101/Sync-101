package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.PullRequest;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PullRequestRepository extends JpaRepository<PullRequest, Long> {

    @Query("SELECT pr FROM PullRequest pr " +
            "WHERE pr.createdAt >= :startDateTime " +
            "AND pr.createdAt <= :endDateTime " +
            "AND pr.utilisateurId IN :userIds")
    List<PullRequest> findByFilters(@Param("startDateTime") LocalDateTime startDateTime,
                                    @Param("endDateTime") LocalDateTime endDateTime,
                                    @Param("userIds") List<Long> userIds);
}
