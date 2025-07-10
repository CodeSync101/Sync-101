package com.example.backend.Repository;

import com.example.backend.Entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    @Query("SELECT c.author.name, COUNT(c) FROM Commit c GROUP BY c.author.name ORDER BY COUNT(c) DESC")
    List<Object[]> findTopAuthorByCommitCount();
    @Query("SELECT c.author.name, COUNT(c) FROM Commit c GROUP BY c.author.name ORDER BY COUNT(c) DESC")
    List<Object[]> findAuthorByCommitCount();



}