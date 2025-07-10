package com.example.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Repository {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String owner;
@JsonIgnore
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Branch> branches;
@JsonIgnore
    @OneToMany(mappedBy = "repository", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pull> pullRequests;

    // Getters and Setters
    public Long getId() { return id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getOwner() { return owner; }

    public void setOwner(String owner) { this.owner = owner; }

    public List<Branch> getBranches() { return branches; }

    public void setBranches(List<Branch> branches) { this.branches = branches; }

    public List<Pull> getPullRequests() { return pullRequests; }

    public void setPullRequests(List<Pull> pullRequests) { this.pullRequests = pullRequests; }
}
