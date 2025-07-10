package com.example.backend.Service;

import com.example.backend.Entity.Author;
import com.example.backend.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatsService {

    @Autowired private RepositoryRepository repositoryRepo;
    @Autowired private BranchRepository branchRepo;
    @Autowired private CommitRepository commitRepo;
    @Autowired private PullRepository pullRepo;
    @Autowired private AuthorRepository authorRepo;

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalRepositories", repositoryRepo.count());
        stats.put("totalBranches", branchRepo.count());
        stats.put("totalCommits", commitRepo.count());
        stats.put("totalPullRequests", pullRepo.count());

        stats.put("mergedBranches", branchRepo.countByIsMergedTrue());
        stats.put("nonMergedBranches", branchRepo.countByIsMergedFalse());

        // Most active author
        List<Object[]> authors = commitRepo.findTopAuthorByCommitCount();
        if (!authors.isEmpty()) {
            Object[] topAuthor = authors.get(0);
            stats.put("topAuthor", Map.of(
                    "name", topAuthor[0],
                    "commitCount", topAuthor[1]
            ));
        }

        // All authors with commit count
        List<Object[]> results = commitRepo.findAuthorByCommitCount();
        List<Map<String, Object>> authorsStats = new ArrayList<>();

        for (Object[] row : results) {
            String name = (String) row[0];
            Long count = (Long) row[1];

            authorsStats.add(Map.of(
                    "name", name,
                    "commitCount", count
            ));
        }

        stats.put("authors", authorsStats); // ✅ Add this line

        return stats;
    }
}
