package feten.tn.projetmission.Entities.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTacheDTO {

    private Long id;
    private String titre;
    private String status;
    private float notetache;

    private Long matiereId;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public float getNotetache() {
        return notetache;
    }

    public void setNotetache(float notetache) {
        this.notetache = notetache;
    }

    public Long getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(Long matiereId) {
        this.matiereId = matiereId;
    }
}
