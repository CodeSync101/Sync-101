package com.example.backend.Service;

import com.example.backend.Repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryService {

    private final BranchRepository branchRepository;

    public RepositoryService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<String> getAllRepositories() {
        return branchRepository.findDistinctRepositoryNames();
    }
}
