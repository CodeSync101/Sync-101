package tn.missionentreprise.rapportservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.missionentreprise.rapportservice.Entity.Commit;
import tn.missionentreprise.rapportservice.Entity.Utilisateur;

import java.sql.Timestamp;
import java.util.List;

/**
 * Repository pour gérer les entités Commit avec des requêtes personnalisées.
 */
@Repository
public interface CommitRepository extends JpaRepository<Commit, Long> {

    /**
     * Récupère le nombre de commits par étudiant (nom, prénom) dans une période donnée.
     *
     * @param startDate Date de début (non null).
     * @param endDate   Date de fin (non null).
     * @param userIds   Liste des IDs d'utilisateurs (non null et non vide).
     * @return Liste d'objets contenant nom, prénom et nombre total de commits.
     */
    @Query("SELECT u.nom AS nom, u.prenom AS prenom, COUNT(c.id) AS totalCommits " +
            "FROM Commit c JOIN c.utilisateur u " +
            "WHERE c.dateCommit >= :startDate " +
            "AND c.dateCommit <= :endDate " +
            "AND u.id IN :userIds " +
            "GROUP BY u.nom, u.prenom")
    List<Object[]> countCommitsByEtudiant(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("userIds") List<Long> userIds
    );

    @Query("SELECT c FROM Commit c WHERE c.utilisateur IN :users")
    List<Commit> findByUsers(@Param("users") List<Utilisateur> users);

    /**
     * Récupère le nombre de commits par date dans une période donnée.
     *
     * @param startDate Date de début (non null).
     * @param endDate   Date de fin (non null).
     * @param userIds   Liste des IDs d'utilisateurs (non null et non vide).
     * @return Liste d'objets contenant la date du commit et le nombre total de commits.
     */
    @Query("SELECT c.dateCommit AS dateCommit, COUNT(c.id) AS totalCommits " +
            "FROM Commit c " +
            "WHERE c.dateCommit >= :startDate " +
            "AND c.dateCommit <= :endDate " +
            "AND c.utilisateur.id IN :userIds " +
            "GROUP BY c.dateCommit " +
            "ORDER BY c.dateCommit")
    List<Object[]> countCommitsByDate(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("userIds") List<Long> userIds
    );
}