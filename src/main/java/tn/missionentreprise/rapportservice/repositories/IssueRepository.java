package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Issue;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IssueRepository extends JpaRepository<Issue, Long> {

    @Query("SELECT i FROM Issue i " +
            "WHERE (COALESCE(:startDate, NULL) IS NULL OR i.createdAt >= :startDate) " +
            "AND (COALESCE(:endDate, NULL) IS NULL OR i.createdAt <= :endDate) " +
            "AND (COALESCE(:userIds, NULL) IS NULL OR i.utilisateurId IN :userIds)")
    List<Issue> findByFilters(@Param("startDate") LocalDateTime startDate,
                              @Param("endDate") LocalDateTime endDate,
                              @Param("userIds") List<Long> userIds);
}
