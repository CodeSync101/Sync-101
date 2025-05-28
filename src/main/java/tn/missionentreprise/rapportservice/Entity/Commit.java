package tn.missionentreprise.rapportservice.Entity;

import jakarta.persistence.*;

// Utilise l’un de ces deux selon ton code :
import java.sql.Timestamp; // Si tu utilises SQL types
// ou mieux :
import java.time.LocalDateTime; // Si tu utilises JPA moderne avec LocalDateTime

import java.util.Date;

@Entity
public class Commit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sha;
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_commit")
    private Timestamp dateCommit;
    private int fichiersChanges;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    public Commit(Long id, String sha, String message, Timestamp dateCommit, int fichiersChanges, Utilisateur utilisateur) {
        this.id = id;
        this.sha = sha;
        this.message = message;
        this.dateCommit = dateCommit;
        this.fichiersChanges = fichiersChanges;
        this.utilisateur = utilisateur;
    }

    public Commit() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateCommit() {
        return dateCommit;
    }

    public void setDateCommit(Timestamp dateCommit) {
        this.dateCommit = dateCommit;
    }

    public int getFichiersChanges() {
        return fichiersChanges;
    }

    public void setFichiersChanges(int fichiersChanges) {
        this.fichiersChanges = fichiersChanges;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }
}