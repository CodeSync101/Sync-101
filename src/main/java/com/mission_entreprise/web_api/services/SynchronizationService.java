package com.mission_entreprise.web_api.services;


import com.mission_entreprise.web_api.dtos.DistinctBranchDTO;
import com.mission_entreprise.web_api.dtos.GitHubEventDto;
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
    private final GitHubEventService gitHubEventService ;

    @Scheduled(fixedRate = 1 * 60 * 1000)
    public void synchronize() {
        // 1. Commit sync per branch
        List<Branch> branches = branchRepository.findAll();
        if (branches == null || branches.isEmpty()) {
            log.warn("No branch data found in the database.");
        } else {
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
        }

        // 2. Event sync per distinct org/repo
        List<DistinctBranchDTO> distinctBranches = branchRepository.getDistinctBranches();
        if (distinctBranches == null || distinctBranches.isEmpty()) {
            log.warn("No distinct branch data found in the database.");
        } else {
            log.info("Starting event synchronization for {} distinct org/repo pairs", distinctBranches.size());
            for (DistinctBranchDTO dto : distinctBranches) {
                try {
                    String org = dto.getOrganization();
                    String repo = dto.getRepositoryName();

                    log.info("Fetching GitHub events for Org: {}, Repo: {}", org, repo);
                    List<GitHubEventDto> events = gitHubEventService.fetchOrgRepoEvents(org, repo);
                    log.info("Fetched {} events for Org: {}, Repo: {}", events.size(), org, repo);
                } catch (Exception e) {
                    log.error("Error syncing events for Org: {}, Repo: {}: {}", dto.getOrganization(), dto.getRepositoryName(), e.getMessage());
                }
            }
        }

        log.info("Synchronization completed");
    }

}
