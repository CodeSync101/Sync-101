package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Long> {
    Optional<Branch> findByName(String name);

}
