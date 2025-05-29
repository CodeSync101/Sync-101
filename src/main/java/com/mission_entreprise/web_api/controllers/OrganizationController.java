package com.mission_entreprise.web_api.controllers;


import com.mission_entreprise.web_api.entities.GithubOrganization;
import com.mission_entreprise.web_api.services.GithubService;
import com.mission_entreprise.web_api.services.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;
    private final GithubService githubService ;

    @PostMapping("/saveOrganization")
    public GithubOrganization getUser(@RequestParam String orgName) {
        return organizationService.saveOrganization(orgName) ;
    }
    @GetMapping("/all")
    public ResponseEntity<List<String>> getAllOrganizations() {
        return new ResponseEntity<>( organizationService.listOrganizations(), HttpStatus.OK);
    }
}
