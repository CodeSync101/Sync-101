package com.example.backend.Repository;

import com.example.backend.Entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("SELECT DISTINCT b.repositoryName FROM Branch b")
    List<String> findDistinctRepositoryNames();

    List<Branch> findByRepositoryName(String repositoryName);
}
