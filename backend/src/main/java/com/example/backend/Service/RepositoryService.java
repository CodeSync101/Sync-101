package com.example.backend.Service;

import com.example.backend.Entity.Repository;
import com.example.backend.Repository.BranchRepository;
import com.example.backend.Repository.RepositoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RepositoryService {

    private final RepositoryRepository repositoryRepository;

    public RepositoryService(RepositoryRepository repositoryRepository) {
        this.repositoryRepository = repositoryRepository;
    }


    public List<Repository> getAllRepositories() {
        return repositoryRepository.findAll();
    }
}
