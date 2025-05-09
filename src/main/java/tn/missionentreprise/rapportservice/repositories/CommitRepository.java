package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Commit;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {

    @Query("SELECT u.nom, u.prenom, COUNT(c.id) " +
            "FROM Commit c JOIN c.utilisateur u " +
            "GROUP BY u.nom, u.prenom")
    List<Object[]> countCommitsByEtudiant();

    @Query(value = "SELECT CAST(c.date_commit AS DATE), COUNT(c.id) " +
            "FROM commit c " +
            "GROUP BY CAST(c.date_commit AS DATE) " +
            "ORDER BY CAST(c.date_commit AS DATE)", nativeQuery = true)
    List<Object[]> countCommitsByDate();


}