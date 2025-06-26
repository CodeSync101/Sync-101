package com.mission_entreprise.web_api.entities.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "commit_report")
public class CommitReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sha;
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_commit")
    private Date dateCommit;
    private int fichiersChanges;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


}
