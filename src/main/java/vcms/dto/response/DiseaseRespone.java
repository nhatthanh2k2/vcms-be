package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class DiseaseRespone {
    private Long diseaseId;

    private String diseaseName;

    private String diseaseDescription;

    private String diseasePreventive;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseUpdateAt;

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
