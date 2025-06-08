package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.BranchResponse;
import com.mission_entreprise.web_api.entities.Branch;
import com.mission_entreprise.web_api.repositories.BranchRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BranchService {

    private final GitHubApiClient gitHubApiClient;
    private final BranchRepository branchRepository;

    public void syncBranches(String org, String repo) {
        BranchResponse[] branches = gitHubApiClient.fetchBranchesByOrgAndRepo(org, repo);

        for (BranchResponse branchDto : branches) {
            branchRepository.findByNameAndRepositoryName(branchDto.getName(), repo).ifPresentOrElse(
                    (existing) -> log.info("Branch '{}' already exists in repository '{}'", existing.getName(), repo),
                    () -> {
                        Branch branch = new Branch();
                        branch.setName(branchDto.getName());
                        branch.setProtected(branchDto.isProtected());
                        branch.setRepositoryName(repo);
                        branchRepository.save(branch);
                        log.info("Saved new branch: {}", branch.getName());
                    }
            );
        }

    }

    public long getDistinctBranchCount(String organization) {
        return branchRepository.countDistinctByName(organization);
    }


}
