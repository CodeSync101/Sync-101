package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Branch;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query(value = """
        SELECT b.name AS branchName, u.nom AS nom, u.prenom AS prenom, c.date_commit AS dateCommit
        FROM branch b
        JOIN commit c ON b.commit_id = c.id
        JOIN utilisateur u ON c.utilisateur_id = u.id
        WHERE (:startDate IS NULL OR c.date_commit >= :startDate)
          AND (:endDate IS NULL OR c.date_commit <= :endDate)
          AND (:userIds IS NULL OR u.id IN (:userIds))
    """, nativeQuery = true)
    List<Object[]> findBranchesWithUserDetailsNative(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("userIds") List<Long> userIds
    );

    @Query(value = """
        SELECT DATE(c.date_commit) AS creationDate, COUNT(b.id) AS branchCount
        FROM branch b
        JOIN commit c ON b.commit_id = c.id
        WHERE (:startDate IS NULL OR c.date_commit >= :startDate)
          AND (:endDate IS NULL OR c.date_commit <= :endDate)
          AND (:userIds IS NULL OR c.utilisateur_id IN (:userIds))
        GROUP BY DATE(c.date_commit)
        ORDER BY DATE(c.date_commit)
    """, nativeQuery = true)
    List<Object[]> countBranchesByDateNative(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("userIds") List<Long> userIds
    );
}
