package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Commit;
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

}
