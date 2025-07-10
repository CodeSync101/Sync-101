package com.example.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private boolean isMerged = false;

    public boolean isMerged() {
        return isMerged;
    }

    public void setIsMerged(boolean isMerged) {
        this.isMerged = isMerged;
    }
    private String name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    @JsonIgnore
    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commit> commits;

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Repository getRepository() { return repository; }

    public void setRepository(Repository repository) { this.repository = repository; }

    public List<Commit> getCommits() { return commits; }

    public void setCommits(List<Commit> commits) { this.commits = commits; }
}
