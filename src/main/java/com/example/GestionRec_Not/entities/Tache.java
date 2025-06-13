package com.example.GestionRec_Not.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Tache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titre;
    private String description;
    private LocalDateTime dateCreation;
    private LocalDateTime dateEcheance;
    private Boolean terminee = false;
    
    @ManyToOne
    @JoinColumn(name = "matiere_id")
    private Matiere matiere;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Userr user;
    
    // Constructeur par défaut
    public Tache() {
        this.dateCreation = LocalDateTime.now();
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitre() {
        return titre;
    }
    
    public void setTitre(String titre) {
        this.titre = titre;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
    
    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }
    
    public LocalDateTime getDateEcheance() {
        return dateEcheance;
    }
    
    public void setDateEcheance(LocalDateTime dateEcheance) {
        this.dateEcheance = dateEcheance;
    }
    
    public Boolean getTerminee() {
        return terminee;
    }
    
    public void setTerminee(Boolean terminee) {
        this.terminee = terminee;
    }
    
    public Matiere getMatiere() {
        return matiere;
    }
    
    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }
    
    public Userr getUser() {
        return user;
    }
    
    public void setUser(Userr user) {
        this.user = user;
    }
} 