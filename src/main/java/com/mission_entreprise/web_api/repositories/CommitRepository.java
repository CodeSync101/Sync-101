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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Repository
public interface CommitRepository extends JpaRepository<Commit,Long> {

    boolean existsByAuthorAndDate(String author, String date);
    long count();

    @Query("SELECT c.author FROM Commit c WHERE c.organization =:organization GROUP BY c.author ")
    List<String> findDistinctAuthors(@Param("organization")String organization);

    @Query("SELECT COUNT(DISTINCT c.author) FROM Commit c WHERE c.organization =:organization")
    Long countDistinctAuthors(@Param("organization")String organization);

    @Query("SELECT DISTINCT LOWER(TRIM(c.repositoryName)) FROM Commit c WHERE c.organization =:organization AND c.repositoryName IS NOT NULL AND TRIM(c.repositoryName) <> ''")
    List<String> findDistinctSanitizedRepositoryNames(@Param("organization") String organization);

    @Query("SELECT COUNT(DISTINCT LOWER(TRIM(c.repositoryName))) FROM Commit c WHERE c.organization=:organization AND c.repositoryName IS NOT NULL AND TRIM(c.repositoryName) <> ''")
    Long countDistinctSanitizedRepositories(@Param("organization") String organization);

    @Query("SELECT SUBSTRING(c.date, 1, 10) as commitDate, COUNT(c) as commitCount " +
            "FROM Commit c " +
            "WHERE c.date >= :startDate AND c.date <= :endDate " +
            "AND (:author IS NULL OR c.author = :author) " +
            "AND c.organization=:organization  "+
            "GROUP BY SUBSTRING(c.date, 1, 10) " +
            "ORDER BY commitDate")
    List<Object[]> countCommitsByDateBetweenAndAuthor(String startDate, String endDate, String author, String organization);



    @Query("select new com.mission_entreprise.web_api.dtos.EventAnalyticsDTO(c.date, c.author, c.htmlUrl, 'Commit') " +
            "from Commit c " +
            "where c.organization = :organization " +
            "order by c.date desc")
    List<EventAnalyticsDTO> findEventsDetailsCommit(@Param("organization") String organization, Pageable pageable);

    @Query("select new com.mission_entreprise.web_api.dtos.PushEventDTO(g.createdAt, g.actorLogin, g.eventId, 'Push') from GitHubEventEntity g where g.organization= :organization order by g.createdAt desc ")
    List<PushEventDTO> findEventsDetailsPush(@Param("organization") String organization,Pageable pageable);
    @Query("select new com.mission_entreprise.web_api.dtos.PullMergeDTO(p.createdAt, p.author, p.htmlUrl, p.state, p.mergedAt, 'Pull', p.organization) " +
            "from Pull p where p.organization = :organization order by p.createdAt desc")
    List<PullMergeDTO> findEventsDetailsPulls(@Param("organization") String organization, Pageable pageable);


    @Query("select count(*) from Commit c where c.organization =:organization")
    Long getCommitByOrganizationCount(@Param("organization") String organization);

    @Query("SELECT c.date FROM Commit c WHERE c.organization = :organization")
    List<String> findAllCommitDatesByOrganization(@Param("organization") String organization);

    @Query("SELECT c.repositoryName, COUNT(c) FROM Commit c WHERE c.organization = :organization AND c.repositoryName IS NOT NULL GROUP BY c.repositoryName")
    List<Object[]> countCommitsPerRepository(@Param("organization") String organization);


}
