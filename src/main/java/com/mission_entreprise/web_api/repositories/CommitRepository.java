package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.dtos.EventAnalyticsDTO;
import com.mission_entreprise.web_api.dtos.PullMergeDTO;
import com.mission_entreprise.web_api.dtos.PushEventDTO;
import com.mission_entreprise.web_api.entities.Branch;
import com.mission_entreprise.web_api.entities.Commit;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit,Long> {

    boolean existsByAuthorAndDate(String author, String date);
    long count();

    @Query("SELECT c.author FROM Commit c GROUP BY c.author")
    List<String> findDistinctAuthors();

    @Query("SELECT COUNT(DISTINCT c.author) FROM Commit c")
    Long countDistinctAuthors();

    @Query("SELECT DISTINCT LOWER(TRIM(c.repositoryName)) FROM Commit c WHERE c.repositoryName IS NOT NULL AND TRIM(c.repositoryName) <> ''")
    List<String> findDistinctSanitizedRepositoryNames();

    @Query("SELECT COUNT(DISTINCT LOWER(TRIM(c.repositoryName))) FROM Commit c WHERE c.repositoryName IS NOT NULL AND TRIM(c.repositoryName) <> ''")
    Long countDistinctSanitizedRepositories();

    @Query("SELECT SUBSTRING(c.date, 1, 10) as commitDate, COUNT(c) as commitCount " +
            "FROM Commit c " +
            "WHERE c.date >= :startDate AND c.date <= :endDate " +
            "AND (:author IS NULL OR c.author = :author) " +
            "GROUP BY SUBSTRING(c.date, 1, 10) " +
            "ORDER BY commitDate")
    List<Object[]> countCommitsByDateBetweenAndAuthor(String startDate, String endDate, String author);



    @Query("select new com.mission_entreprise.web_api.dtos.EventAnalyticsDTO(c.date, c.author, c.htmlUrl, 'Commit') from Commit c order by c.date desc")
    List<EventAnalyticsDTO> findEventsDetailsCommit(Pageable pageable);
    @Query("select new com.mission_entreprise.web_api.dtos.PushEventDTO(g.createdAt, g.actorLogin, g.eventId, 'Push') from GitHubEventEntity g order by g.createdAt desc ")
    List<PushEventDTO> findEventsDetailsPush(Pageable pageable);
    @Query("select new com.mission_entreprise.web_api.dtos.PullMergeDTO(p.createdAt,p.author,p.htmlUrl,p.state,p.mergedAt,'Pull') from Pull p order by p.createdAt desc")
    List<PullMergeDTO> findEventsDetailsPulls(Pageable pageable);





}
