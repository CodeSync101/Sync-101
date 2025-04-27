package com.mission_entreprise.web_api.services;


import com.mission_entreprise.web_api.entities.GithubOrganization;
import com.mission_entreprise.web_api.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository ;
    private final GithubService githubService ;

    public GithubOrganization saveOrganization(String orgName) {
        try {
            Optional<GithubOrganization> existingOrg = organizationRepository.findByLogin(orgName);
            if (existingOrg.isPresent()) {
                return existingOrg.get();
            }

            GithubOrganization githubOrganization = githubService.getOrganization(orgName);
            return organizationRepository.save(githubOrganization);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }




}
