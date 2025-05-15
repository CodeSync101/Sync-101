package feten.tn.projetmission.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Tache {
    @Id
    @GeneratedValue
    private Long id;
    private String titre;
    private String status;
    private float notetache;

    @ManyToOne
    @JsonIgnore
    private Matiere matiere;

    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL)
    private List<Ticket> tickets = new ArrayList<>();

    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public String getTitre() {
        return titre;
    }



    public String getStatus() {
        return status;
    }


    public float getNotetache() {
        return notetache;
    }

    public void setNotetache(float notetache) {
        this.notetache = notetache;
    }

    public Matiere getMatiere() {
        return matiere;
    }



    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }
}
