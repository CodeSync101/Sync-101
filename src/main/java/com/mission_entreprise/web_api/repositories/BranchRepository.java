package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    Optional<Branch> findByNameAndRepositoryName(String name, String repositoryName);

    @Query("SELECT COUNT(DISTINCT b.name) FROM Branch b")
    long countDistinctByName();



}
