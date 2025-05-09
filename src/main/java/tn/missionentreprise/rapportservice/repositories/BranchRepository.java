package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Branch;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query(value = "SELECT b.name, u.nom, u.prenom, c.date_commit " +
            "FROM branch b " +
            "JOIN commit c ON b.commit_id = c.id " +
            "JOIN utilisateur u ON c.utilisateur_id = u.id", nativeQuery = true)
    List<Object[]> findBranchesWithUserDetails();

    @Query(value = "SELECT DATE(c.date_commit) as creation_date, COUNT(b.id) as branch_count " +
            "FROM branch b " +
            "JOIN commit c ON b.commit_id = c.id " +
            "GROUP BY DATE(c.date_commit) " +
            "ORDER BY DATE(c.date_commit)", nativeQuery = true)
    List<Object[]> countBranchesByDate();
}