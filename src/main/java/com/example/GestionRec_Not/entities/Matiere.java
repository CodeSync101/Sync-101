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
    
    private String nomMatiere;
    private Float noteMatiere;
    
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Userr etudiant;
    
    // Constructeurs
    public Matiere() {
    }
    
    public Matiere(String nomMatiere, Float noteMatiere) {
        this.nomMatiere = nomMatiere;
        this.noteMatiere = noteMatiere;
    }
    
    // Getters et Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNomMatiere() {
        return nomMatiere;
    }
    
    public void setNomMatiere(String nomMatiere) {
        this.nomMatiere = nomMatiere;
    }
    
    public Float getNoteMatiere() {
        return noteMatiere;
    }
    
    public void setNoteMatiere(Float noteMatiere) {
        this.noteMatiere = noteMatiere;
    }
    
    public Userr getEtudiant() {
        return etudiant;
    }
    
    public void setEtudiant(Userr etudiant) {
        this.etudiant = etudiant;
    }
} 