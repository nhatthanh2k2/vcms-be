package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vaccines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaccine {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "vaccine_seq")
    @SequenceGenerator(name = "vaccine_seq", sequenceName = "vaccine_seq",
            allocationSize = 1, initialValue = 1000)
    @Column(name = "vac_id")
    private Long vaccineId;

    @Column(name = "vac_code")
    private String vaccineCode;

    @Column(name = "vac_name")
    private String vaccineName;

    @Column(name = "vac_image")
    private String vaccineImage;

    //Cong dung
    @Lob
    @Column(name = "vac_purpose", columnDefinition = "TEXT")
    private String vaccinePurpose;

    // Nguồn gốc
    @Column(name = "vac_origin")
    private String vaccineOrigin;

    // Đường tiêm
    @Lob
    @Column(name = "vac_inj_route", columnDefinition = "TEXT")
    private String vaccineInjectionRoute;

    // Chống chỉ định
    @Lob
    @Column(name = "vac_contraind", columnDefinition = "TEXT")
    private String vaccineContraindication;

    // Phản ứng sau tiêm
    @Lob
    @Column(name = "vac_post_inj_react", columnDefinition = "TEXT")
    private String vaccineReaction;

    //So mui tre em
    @Column(name = "vac_child_dose_count")
    private int vaccineChildDoseCount;

    //So mui nguoi lon
    @Column(name = "vac_adult_dose_count")
    private int vaccineAdultDoseCount;

    // Bảo quản
    @Lob
    @Column(name = "vac_storage", columnDefinition = "TEXT")
    private String vaccineStorage;

    //Phac do tiem
    @Lob
    @Column(name = "vac_inj_sched", columnDefinition = "TEXT")
    private String vaccineInjectionSchedule;

    // Đối tượng
    @Lob
    @Column(name = "vac_patient", columnDefinition = "TEXT")
    private String vaccinePatient;

    @Column(name = "vac_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineCreateAt;

    @Column(name = "vac_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineUpdateAt;

    @ManyToOne
    @JoinColumn(name = "disease_id")
    @JsonIgnore
    private Disease disease;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    @JsonIgnore
    private List<BatchDetail> batchDetailList = new ArrayList<>();

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VaccinePackageDetail> vaccinePackageDetails = new ArrayList<>();
}
