package com.example.backend.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Commit {

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }


@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(columnDefinition = "TEXT")

    private String sha;
    @Column(columnDefinition = "TEXT")

    private String message;

@JsonIgnore
    @ManyToOne
    @JoinColumn(name = "branch_id")
    private Branch branch;

    // Getters and Setters
    public Long getId() { return id; }

    public String getSha() { return sha; }

    public void setSha(String sha) { this.sha = sha; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }



    public Branch getBranch() { return branch; }

    public void setBranch(Branch branch) { this.branch = branch; }
}
