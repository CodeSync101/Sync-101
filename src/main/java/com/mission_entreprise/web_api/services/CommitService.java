package com.mission_entreprise.web_api.services;

import com.mission_entreprise.web_api.dtos.*;
import com.mission_entreprise.web_api.entities.Commit;
import com.mission_entreprise.web_api.repositories.CommitRepository;
import com.mission_entreprise.web_api.utils.GitHubApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommitService {

    private final GitHubApiClient gitHubApiClient;
    private final CommitRepository commitRepository;


    public List<EventAnalyticsDTO> getAllCommitsAnalyticsLatest(String organization) {
        Pageable topFive = PageRequest.of(0, 50);
        return commitRepository.findEventsDetailsCommit(organization, topFive);
    }
    public List<PushEventDTO> getAllPushAnalyticsLatest(String organization) {
        Pageable topFive = PageRequest.of(0, 50);

        return commitRepository.findEventsDetailsPush(organization,topFive);
    }
    public List<PullMergeDTO> getAllPullAnalyticsLatest() {
        Pageable topFive = PageRequest.of(0, 50);
        return commitRepository.findEventsDetailsPulls(topFive);
    }

    public ContributionSummaryDTO getContributionSummary(int topLimit,String organization) {
        List<EventAnalyticsDTO> commits = getAllCommitsAnalyticsLatest(organization);
        List<PushEventDTO> pushes = getAllPushAnalyticsLatest(organization);
        List<PullMergeDTO> pulls = getAllPullAnalyticsLatest();

        Map<String, Integer> contributionsByAuthor = new HashMap<>();

        commits.forEach(c -> contributionsByAuthor.merge(c.getAuthor(), 1, Integer::sum));
        pushes.forEach(p -> contributionsByAuthor.merge(p.getAuthor(), 1, Integer::sum));
        pulls.forEach(p -> contributionsByAuthor.merge(p.getAuthor(), 1, Integer::sum));

        int totalContributions = contributionsByAuthor.values().stream().mapToInt(Integer::intValue).sum();

        List<ContributionSummaryDTO.ContributorDTO> topContributors = contributionsByAuthor.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(topLimit)
                .map(entry -> new ContributionSummaryDTO.ContributorDTO(entry.getKey(), entry.getValue()))
                .toList();

        return new ContributionSummaryDTO(totalContributions, topContributors);
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
                    commit.setOrganization(orgName);
                    commitRepository.save(commit);
                }
            }
        }
    }
    public long getTotalCommitsCount(String organization) {
        return commitRepository.getCommitByOrganizationCount(organization);
    }
    public Map<String, Object> getDistinctRepositoriesWithCount(String organization) {
        List<String> repositories = commitRepository.findDistinctSanitizedRepositoryNames(organization);
        Long count = commitRepository.countDistinctSanitizedRepositories(organization);

        Map<String, Object> result = new HashMap<>();
        result.put("repositories", repositories);
        result.put("repositoryCount", count);
        return result;
    }
    public Map<String, Object> getDistinctAuthorsWithCount(String organization) {
        List<String> authors = commitRepository.findDistinctAuthors(organization);
        Long authorsCount = commitRepository.countDistinctAuthors(organization);

        Map<String, Object> result = new HashMap<>();
        result.put("authors", authors);
        result.put("authorsCount", authorsCount);

        return result;
    }
    public Map<String, Long> getCommitCountsByDateRange(String startDate, String endDate, String author,String organization) {
        if (!startDate.matches("\\d{4}-\\d{2}-\\d{2}") || !endDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Date format must be yyyy-MM-dd");
        }
        String formattedStartDate = startDate + "T00:00:00Z";
        String formattedEndDate = endDate + "T23:59:59Z";

        List<Object[]> results = commitRepository.countCommitsByDateBetweenAndAuthor(
                formattedStartDate,
                formattedEndDate,
                author.isEmpty() ? null : author,
                organization
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
