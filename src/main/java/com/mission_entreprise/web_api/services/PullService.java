package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.PullResponse;
import com.mission_entreprise.web_api.entities.Pull;
import com.mission_entreprise.web_api.repositories.PullRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PullService {

    private final GitHubApiClient gitHubApiClient;
    private final PullRepository pullRepository;


    public void savePullRequestsByOrgAndRepo(String org, String repo) {
        PullResponse[] pullResponses = gitHubApiClient.fetchPullRequestsByOrgAndRepo(org, repo);

        if (pullResponses != null) {
            for (PullResponse pr : pullResponses) {
                String author = pr.getUser().getLogin();
                String createdAt = pr.getCreatedAt();

                if (!pullRepository.existsByAuthorAndCreatedAt(author, createdAt)) {
                    Pull pull = new Pull();
                    pull.setAuthor(author);
                    pull.setCreatedAt(createdAt);
                    pull.setTitle(pr.getTitle());
                    pull.setRepositoryName(repo);
                    pull.setHtmlUrl(pr.getHtmlUrl());
                    pull.setState(pr.getState());
                    pull.setMergedAt(pr.getMerged_at());
                    pullRepository.save(pull);
                }
            }
        }
    }
}
