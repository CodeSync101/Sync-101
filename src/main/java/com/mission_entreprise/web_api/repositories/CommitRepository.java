package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    @Query("SELECT COUNT(DISTINCT LOWER(c.repositoryName)) FROM Commit c")
    Long countDistinctRepositories();

    @Query("SELECT DISTINCT LOWER(c.repositoryName) FROM Commit c")
    List<String> findDistinctRepositoryNames();


}
