package vcms.dto.request;

public class DiseaseRequest {

    private String diseaseName;

    private String diseaseDescription;

    private String diseasePreventive;

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
}
