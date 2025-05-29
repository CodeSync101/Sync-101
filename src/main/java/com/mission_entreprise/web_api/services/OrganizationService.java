package com.mission_entreprise.web_api.services;


import com.mission_entreprise.web_api.entities.GithubOrganization;
import com.mission_entreprise.web_api.repositories.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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


    public List<String> listOrganizations() {
        return organizationRepository.findAll().stream().map(GithubOrganization::getLogin).collect(Collectors.toList());
    }

}
