package com.example.GestionRec_Not.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Matiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String libelle;
    private String description;
    private float noteMatiere;
    
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Userr etudiant;
    
    // Constructeurs
    public Matiere() {
    }
    
    public Matiere(String libelle, String description, float noteMatiere) {
        this.libelle = libelle;
        this.description = description;
        this.noteMatiere = noteMatiere;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getLibelle() {
        return libelle;
    }
    
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public float getNoteMatiere() {
        return noteMatiere;
    }
    
    public void setNoteMatiere(float noteMatiere) {
        this.noteMatiere = noteMatiere;
    }
    
    public Userr getEtudiant() {
        return etudiant;
    }
    
    public void setEtudiant(Userr etudiant) {
        this.etudiant = etudiant;
    }
} 