package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.Pull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PullRepository extends JpaRepository<Pull, Long> {
    boolean existsByAuthorAndCreatedAt(String author, String createdAt);
}