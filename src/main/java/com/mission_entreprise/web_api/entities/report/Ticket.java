package com.mission_entreprise.web_api.entities.report;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jiraid;
    private String title;
    private String description;
    private String status;
    private LocalDateTime lastupdated;
    private Long statususer_id;
    private Long assigneduser_id;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getJiraid() { return jiraid; }
    public void setJiraid(String jiraid) { this.jiraid = jiraid; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getLastupdated() { return lastupdated; }
    public void setLastupdated(LocalDateTime lastupdated) { this.lastupdated = lastupdated; }
    public Long getStatususer_id() { return statususer_id; }
    public void setStatususer_id(Long statususer_id) { this.statususer_id = statususer_id; }
    public Long getAssigneduser_id() { return assigneduser_id; }
    public void setAssigneduser_id(Long assigneduser_id) { this.assigneduser_id = assigneduser_id; }
}