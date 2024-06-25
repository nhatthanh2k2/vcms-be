package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "diseases")
public class Disease {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "disease_id")
    private Long diseaseId;

    @Column(name = "disease_name")
    private String diseaseName;

    @Column(name = "disease_description")
    private String diseaseDescription;

    @Column(name = "disease_preventive")
    private String diseasePreventive;

    @Column(name = "disease_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseCreateAt;

    @Column(name = "disease_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseUpdateAt;

    @OneToMany(mappedBy = "disease", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<Vaccine> vaccines = new ArrayList<>();

    public Disease() {
    }

    public Disease(String diseaseName, String diseaseDescription,
                   String diseasePreventive, LocalDateTime diseaseCreateAt,
                   LocalDateTime diseaseUpdateAt) {
        this.diseaseName = diseaseName;
        this.diseaseDescription = diseaseDescription;
        this.diseasePreventive = diseasePreventive;
        this.diseaseCreateAt = diseaseCreateAt;
        this.diseaseUpdateAt = diseaseUpdateAt;
    }

    public Disease(Long diseaseId, String diseaseName,
                   String diseaseDescription,
                   String diseasePreventive, LocalDateTime diseaseCreateAt,
                   LocalDateTime diseaseUpdateAt, List<Vaccine> vaccines) {
        this.diseaseId = diseaseId;
        this.diseaseName = diseaseName;
        this.diseaseDescription = diseaseDescription;
        this.diseasePreventive = diseasePreventive;
        this.diseaseCreateAt = diseaseCreateAt;
        this.diseaseUpdateAt = diseaseUpdateAt;
        this.vaccines = vaccines;
    }

    public Long getDiseaseId() {
        return diseaseId;
    }

    public void setDiseaseId(Long diseaseId) {
        this.diseaseId = diseaseId;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDiseaseDescription() {
        return diseaseDescription;
    }

    public void setDiseaseDescription(String diseaseDescription) {
        this.diseaseDescription = diseaseDescription;
    }

    public String getDiseasePreventive() {
        return diseasePreventive;
    }

    public void setDiseasePreventive(String diseasePreventive) {
        this.diseasePreventive = diseasePreventive;
    }

    public List<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(List<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public LocalDateTime getDiseaseCreateAt() {
        return diseaseCreateAt;
    }

    public void setDiseaseCreateAt(LocalDateTime diseaseCreateAt) {
        this.diseaseCreateAt = diseaseCreateAt;
    }

    public LocalDateTime getDiseaseUpdateAt() {
        return diseaseUpdateAt;
    }

    public void setDiseaseUpdateAt(LocalDateTime diseaseUpdateAt) {
        this.diseaseUpdateAt = diseaseUpdateAt;
    }
}
