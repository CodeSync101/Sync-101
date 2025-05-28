package tn.missionentreprise.rapportservice.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "branch")
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commit_id", referencedColumnName = "id")
    private Commit commit;

    @Column(name = "protected")
    private Boolean isProtected;

    // Constructeurs
    public Branch() {}

    public Branch(String name, Commit commit, Boolean isProtected) {
        this.name = name;
        this.commit = commit;
        this.isProtected = isProtected;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commit getCommit() {
        return commit;
    }

    public void setCommit(Commit commit) {
        this.commit = commit;
    }

    public Boolean getProtected() {
        return isProtected;
    }

    public void setProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }
}