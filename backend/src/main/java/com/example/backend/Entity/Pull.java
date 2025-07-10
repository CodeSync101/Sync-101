package com.example.backend.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "pull")
@Getter
@Setter
public class Pull {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String state;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "repository_id")
    private Repository repository;

    // Getters and Setters
    public Long getId() { return id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getState() { return state; }

    public void setState(String state) { this.state = state; }

    public Repository getRepository() { return repository; }

    public void setRepository(Repository repository) { this.repository = repository; }
}
