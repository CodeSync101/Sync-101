package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.CommitResponse;
import com.mission_entreprise.web_api.entities.Commit;
import com.mission_entreprise.web_api.repositories.CommitRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommitService {

    private final GitHubApiClient gitHubApiClient;
    private final CommitRepository commitRepository;

    @Autowired
    public CommitService(GitHubApiClient gitHubApiClient, CommitRepository commitRepository) {
        this.gitHubApiClient = gitHubApiClient;
        this.commitRepository = commitRepository;
    }

    public void saveCommitsByOrgAndRepo(String orgName, String repoName) {
        CommitResponse[] commitResponses = gitHubApiClient.fetchCommitsByOrgAndRepo(orgName, repoName);

        if (commitResponses != null) {

            for (CommitResponse commitResponse : commitResponses) {
                String author = commitResponse.getCommit().getAuthor().getName();
                String date = commitResponse.getCommit().getAuthor().getDate();

                if (!commitRepository.existsByAuthorAndDate(author, date)) {
                    Commit commit = new Commit();
                    commit.setAuthor(author);
                    commit.setDate(date);
                    commit.setMessage(commitResponse.getCommit().getMessage());
                    commit.setRepositoryName(repoName);
                    commit.setHtmlUrl(commitResponse.getHtmlUrl());
                    commitRepository.save(commit);
                }
            }
        }
    }
    public void saveCommitsByOrgAndRepoAndBranch(String orgName, String repoName, String branch) {
        CommitResponse[] commitResponses = gitHubApiClient.fetchCommitsByOrgAndRepoAndBranch(orgName, repoName, branch);

        if (commitResponses != null) {
            for (CommitResponse commitResponse : commitResponses) {
                String author = commitResponse.getCommit().getAuthor().getName();
                String date = commitResponse.getCommit().getAuthor().getDate();

                if (!commitRepository.existsByAuthorAndDate(author, date)) {
                    Commit commit = new Commit();
                    commit.setAuthor(author);
                    commit.setDate(date);
                    commit.setMessage(commitResponse.getCommit().getMessage());
                    commit.setRepositoryName(repoName);
                    commit.setBranchName(branch);
                    commit.setHtmlUrl(commitResponse.getHtmlUrl());
                    commitRepository.save(commit);
                }
            }
        }
    }
    public long getTotalCommitsCount() {
        return commitRepository.count();
    }
    public Map<String, Object> getDistinctRepositoriesWithCount() {
        List<String> repositories = commitRepository.findDistinctSanitizedRepositoryNames();
        Long count = commitRepository.countDistinctSanitizedRepositories();

        Map<String, Object> result = new HashMap<>();
        result.put("repositories", repositories);
        result.put("repositoryCount", count);
        return result;
    }



    public Map<String, Object> getDistinctAuthorsWithCount() {
        List<String> authors = commitRepository.findDistinctAuthors();
        Long authorsCount = commitRepository.countDistinctAuthors();

        Map<String, Object> result = new HashMap<>();
        result.put("authors", authors);
        result.put("authorsCount", authorsCount);

        return result;
    }

    public Map<String, Long> getCommitCountsByDateRange(String startDate, String endDate, String author) {
        // Make sure start and end dates are in the format yyyy-MM-dd
        if (!startDate.matches("\\d{4}-\\d{2}-\\d{2}") || !endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date format must be yyyy-MM-dd");
        }

        // Format the dates to include time for proper comparison (inclusive range)
        String formattedStartDate = startDate + "T00:00:00Z";
        String formattedEndDate = endDate + "T23:59:59Z";

        List<Object[]> results = commitRepository.countCommitsByDateBetweenAndAuthor(
                formattedStartDate,
                formattedEndDate,
                author.isEmpty() ? null : author
        );

        Map<String, Long> commitCountsByDate = new HashMap<>();

        for (Object[] result : results) {
            String date = (String) result[0];
            Long count = ((Number) result[1]).longValue();
            commitCountsByDate.put(date, count);
        }

        return commitCountsByDate;
    }
}
