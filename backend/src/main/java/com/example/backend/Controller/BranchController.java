package com.example.backend.Controller;

import com.example.backend.Entity.Branch;
import com.example.backend.Repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/branches")
@CrossOrigin("*")
public class BranchController {

    @Autowired
    private BranchRepository branchRepository;

    @GetMapping("/{repoName}")
    public List<Branch> getBranches(@PathVariable String repoName) {
        return branchRepository.findByRepositoryName(repoName);
    }
}