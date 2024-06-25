package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vaccines")
public class Vaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccine_seq")
    @SequenceGenerator(name = "vaccine_seq", sequenceName = "vaccine_seq",
            allocationSize = 1, initialValue = 1000)
    @Column(name = "vaccine_id")
    private Long vaccineId;

    @Column(name = "vaccine_code")
    private String vaccineCode;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "vaccine_image")
    private String vaccineImage;

    @Column(name = "vaccine_description")
    private String vaccineDescription;

    // Nguồn gốc
    @Column(name = "vaccine_origin")
    private String vaccineOrigin;

    // Đường tiêm
    @Column(name = "vaccine_injection_route")
    private String vaccineInjectionRoute;

    // Chống chỉ định
    @Column(name = "vaccine_contraindication")
    private String vaccineContraindication;

    // Tương tác thuốc
    @Column(name = "vaccine_drug_interactions")
    private String vaccineDrugInteractions;

    // Tác dụng không mong muốn
    @Column(name = "vaccine_adverse_effects")
    private String vaccineAdverseEffects;

    // Bảo quản
    @Column(name = "vaccine_storage")
    private String vaccineStorage;

    // Đối tượng
    @Column(name = "vaccine_patient")
    private String vaccinePatient;

    // Phản ứng sau tiêm
    @Column(name = "vaccine_reaction")
    private String vaccineReaction;

    @Column(name = "vaccine_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineCreateAt;

    @Column(name = "vaccine_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineUpdateAt;

    @ManyToOne
    @JoinColumn(name = "disease_id")
    @JsonBackReference
    private Disease disease;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<InjectionSchedule> injectionScheduleList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<BatchDetail> batchDetailList = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            }, mappedBy = "vaccines")
    private Set<VaccinePackage> vaccinePackageSet = new HashSet<>();

    public Vaccine() {
    }

    public Vaccine(Long vaccineId, String vaccineCode, String vaccineName,
                   String vaccineImage, String vaccineDescription,
                   String vaccineOrigin, String vaccineInjectionRoute, String vaccineContraindication,
                   String vaccineDrugInteractions, String vaccineAdverseEffects,
                   String vaccineStorage, String vaccinePatient,
                   String vaccineReaction, LocalDateTime vaccineCreateAt,
                   LocalDateTime vaccineUpdateAt, Disease disease,
                   List<InjectionSchedule> injectionScheduleList,
                   List<BatchDetail> batchDetailList,
                   List<VaccinationRecord> vaccinationRecordList,
                   List<Appointment> appointmentList,
                   List<OrderDetail> orderDetailList,
                   Set<VaccinePackage> vaccinePackageSet) {
        this.vaccineId = vaccineId;
        this.vaccineCode = vaccineCode;
        this.vaccineName = vaccineName;
        this.vaccineImage = vaccineImage;
        this.vaccineDescription = vaccineDescription;
        this.vaccineOrigin = vaccineOrigin;
        this.vaccineInjectionRoute = vaccineInjectionRoute;
        this.vaccineContraindication = vaccineContraindication;
        this.vaccineDrugInteractions = vaccineDrugInteractions;
        this.vaccineAdverseEffects = vaccineAdverseEffects;
        this.vaccineStorage = vaccineStorage;
        this.vaccinePatient = vaccinePatient;
        this.vaccineReaction = vaccineReaction;
        this.vaccineCreateAt = vaccineCreateAt;
        this.vaccineUpdateAt = vaccineUpdateAt;
        this.disease = disease;
        this.injectionScheduleList = injectionScheduleList;
        this.batchDetailList = batchDetailList;
        this.vaccinationRecordList = vaccinationRecordList;
        this.appointmentList = appointmentList;
        this.orderDetailList = orderDetailList;
        this.vaccinePackageSet = vaccinePackageSet;
    }

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getVaccineCode() {
        return vaccineCode;
    }

    public void setVaccineCode(String vaccineCode) {
        this.vaccineCode = vaccineCode;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public String getVaccineImage() {
        return vaccineImage;
    }

    public void setVaccineImage(String vaccineImage) {
        this.vaccineImage = vaccineImage;
    }

    public String getVaccineDescription() {
        return vaccineDescription;
    }

    public void setVaccineDescription(String vaccineDescription) {
        this.vaccineDescription = vaccineDescription;
    }

    public String getVaccineOrigin() {
        return vaccineOrigin;
    }

    public void setVaccineOrigin(String vaccineOrigin) {
        this.vaccineOrigin = vaccineOrigin;
    }

    public String getVaccineInjectionRoute() {
        return vaccineInjectionRoute;
    }

    public void setVaccineInjectionRoute(String vaccineInjectionRoute) {
        this.vaccineInjectionRoute = vaccineInjectionRoute;
    }

    public String getVaccineContraindication() {
        return vaccineContraindication;
    }

    public void setVaccineContraindication(String vaccineContraindication) {
        this.vaccineContraindication = vaccineContraindication;
    }

    public String getVaccineDrugInteractions() {
        return vaccineDrugInteractions;
    }

    public void setVaccineDrugInteractions(String vaccineDrugInteractions) {
        this.vaccineDrugInteractions = vaccineDrugInteractions;
    }

    public String getVaccineAdverseEffects() {
        return vaccineAdverseEffects;
    }

    public void setVaccineAdverseEffects(String vaccineAdverseEffects) {
        this.vaccineAdverseEffects = vaccineAdverseEffects;
    }

    public String getVaccineStorage() {
        return vaccineStorage;
    }

    public void setVaccineStorage(String vaccineStorage) {
        this.vaccineStorage = vaccineStorage;
    }

    public String getVaccinePatient() {
        return vaccinePatient;
    }

    public void setVaccinePatient(String vaccinePatient) {
        this.vaccinePatient = vaccinePatient;
    }

    public String getVaccineReaction() {
        return vaccineReaction;
    }

    public void setVaccineReaction(String vaccineReaction) {
        this.vaccineReaction = vaccineReaction;
    }

    public Disease getDisease() {
        return disease;
    }

    public void setDisease(Disease disease) {
        this.disease = disease;
    }

    public List<InjectionSchedule> getInjectionScheduleList() {
        return injectionScheduleList;
    }

    public void setInjectionScheduleList(
            List<InjectionSchedule> injectionScheduleList) {
        this.injectionScheduleList = injectionScheduleList;
    }

    public List<BatchDetail> getBatchDetailList() {
        return batchDetailList;
    }

    public void setBatchDetailList(
            List<BatchDetail> batchDetailList) {
        this.batchDetailList = batchDetailList;
    }

    public List<VaccinationRecord> getVaccinationRecordList() {
        return vaccinationRecordList;
    }

    public void setVaccinationRecordList(
            List<VaccinationRecord> vaccinationRecordList) {
        this.vaccinationRecordList = vaccinationRecordList;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(
            List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(
            List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }

    public Set<VaccinePackage> getVaccinePackageSet() {
        return vaccinePackageSet;
    }

    public void setVaccinePackageSet(
            Set<VaccinePackage> vaccinePackageSet) {
        this.vaccinePackageSet = vaccinePackageSet;
    }

    public LocalDateTime getVaccineCreateAt() {
        return vaccineCreateAt;
    }

    public void setVaccineCreateAt(LocalDateTime vaccineCreateAt) {
        this.vaccineCreateAt = vaccineCreateAt;
    }

    public LocalDateTime getVaccineUpdateAt() {
        return vaccineUpdateAt;
    }

    public void setVaccineUpdateAt(LocalDateTime vaccineUpdateAt) {
        this.vaccineUpdateAt = vaccineUpdateAt;
    }
}
