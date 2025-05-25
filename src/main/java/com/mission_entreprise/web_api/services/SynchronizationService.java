package com.mission_entreprise.web_api.services;


import com.mission_entreprise.web_api.entities.Branch;
import com.mission_entreprise.web_api.repositories.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SynchronizationService {

    private final CommitService commitService ;
    private final BranchRepository branchRepository ;


    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void synchronize() {
        List<Branch> branches = branchRepository.findAll();
        if (branches == null || branches.isEmpty()) {
            throw new RuntimeException("No branch data found in the database.");
        }
        log.info("Starting commit synchronization for {} branches", branches.size());
        for (Branch branch : branches) {
            try {
                String org = branch.getOrganization();
                String repo = branch.getRepositoryName();
                String branchName = branch.getName();

                log.info("Fetching commits for Org: {}, Repo: {}, Branch: {}", org, repo, branchName);
                commitService.saveCommitsByOrgAndRepoAndBranch(org, repo, branchName);
            } catch (Exception e) {
                log.error("Error syncing commits for branch ID {}: {}", branch.getId(), e.getMessage());
            }
        }
        log.info("Synchronization completed");
    }

}
