package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.BranchResponse;
import com.mission_entreprise.web_api.entities.Branch;
import com.mission_entreprise.web_api.repositories.BranchRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void fetchAndSaveBranchesForOrg(String organization) {
        var repos = gitHubApiClient.fetchFromGitHub(
                "https://api.github.com/orgs/" + organization + "/repos",
                BranchResponse[].class);

        if (repos == null || repos.length == 0) {
            log.warn("No repositories found for organization: {}", organization);
            return;
        }

        for (var repo : repos) {
            String repoName = repo.getName();
            log.info("Fetching branches for repo: {}", repoName);

            // 2. Fetch branches for each repo
            BranchResponse[] branches;
            try {
                branches = gitHubApiClient.fetchBranchesByOrgAndRepo(organization, repoName);
            } catch (Exception e) {
                log.error("Failed to fetch branches for repo {}: {}", repoName, e.getMessage());
                continue; // skip this repo
            }

            if (branches == null || branches.length == 0) {
                log.info("No branches found for repo: {}", repoName);
                continue;
            }

            for (BranchResponse branchResponse : branches) {
                String branchName = branchResponse.getName();

                // Check if branch already exists
                boolean exists = branchRepository.existsByNameAndRepositoryName(branchName, repoName);
                if (exists) {
                    log.info("Branch {} for repo {} already exists. Skipping.", branchName, repoName);
                    continue;
                }

                Branch branch = new Branch();
                branch.setName(branchName);
                branch.setProtected(branchResponse.isProtected());
                branch.setRepositoryName(repoName);
                branch.setOrganization(organization);

                try {
                    branchRepository.save(branch);
                } catch (Exception e) {
                    log.error("Failed to save branch {} of repo {}: {}", branch.getName(), repoName, e.getMessage());
                }
            }

        }
    }

}
