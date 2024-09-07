package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Column(name = "vaccine_id")
    private Long vaccineId;

    @Column(name = "vaccine_code")
    private String vaccineCode;

    @Column(name = "vaccine_name")
    private String vaccineName;

    @Column(name = "vaccine_image")
    private String vaccineImage;

    //Cong dung
    @Lob
    @Column(name = "vaccine_purpose", columnDefinition = "TEXT")
    private String vaccinePurpose;

    // Nguồn gốc
    @Column(name = "vaccine_origin")
    private String vaccineOrigin;

    // Đường tiêm
    @Lob
    @Column(name = "vaccine_injection_route", columnDefinition = "TEXT")
    private String vaccineInjectionRoute;

    // Chống chỉ định
    @Lob
    @Column(name = "vaccine_contraindication", columnDefinition = "TEXT")
    private String vaccineContraindication;

    // Phản ứng sau tiêm
    @Lob
    @Column(name = "vaccine_reaction", columnDefinition = "TEXT")
    private String vaccineReaction;

    //So mui tre em
    @Column(name = "vaccine_Childshots")
    private int vaccineChildShots;

    //So mui nguoi lon
    @Column(name = "vaccine_Adultshots")
    private int vaccineAdultShots;

    // Bảo quản
    @Column(name = "vaccine_storage")
    private String vaccineStorage;

    //Phac do tiem
    @Lob
    @Column(name = "vaccine_injection_schedule", columnDefinition = "TEXT")
    private String vaccineInjectionSchedule;

    // Đối tượng
    @Column(name = "vaccine_patient")
    private String vaccinePatient;

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


}
