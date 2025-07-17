package com.mission_entreprise.web_api.repositories;

import com.mission_entreprise.web_api.entities.GithubOrganization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<GithubOrganization,Long>{

    Optional<GithubOrganization> findByLogin(String login);

}
