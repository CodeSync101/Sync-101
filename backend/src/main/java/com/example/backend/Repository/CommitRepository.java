package com.example.backend.Repository;

import com.example.backend.Entity.Commit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommitRepository extends JpaRepository<Commit, Long> {
    boolean existsByRepositoryNameAndBranchName(String repositoryName, String branchName);
}