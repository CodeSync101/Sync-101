package feten.tn.projetmission.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Matiere {
    @Id
    @GeneratedValue
    private Long id;
    private String libelle;
    private String description;
    private float noteMatiere;

    @OneToMany(mappedBy = "matiere", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Tache> taches = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public float getNoteMatiere() {
        return noteMatiere;
    }

    public void setNoteMatiere(float noteMatiere) {
        this.noteMatiere = noteMatiere;
    }

    public List<Tache> getTaches() {
        return taches;
    }

    public void setTaches(List<Tache> taches) {
        this.taches = taches;
    }
}
