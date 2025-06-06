package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    @Column(name = "vaccine_age")
    private Set<String> vaccineAgeRange;

    //Cong dung
    @Lob
    @Column(name = "vaccine_description", columnDefinition = "TEXT")
    private String vaccineDescription;

    // Nguồn gốc
    @Column(name = "vaccine_origin")
    private String vaccineOrigin;

    // Đường tiêm
    @Lob
    @Column(name = "vaccine_route", columnDefinition = "TEXT")
    private String vaccineInjectionRoute;

    // Chống chỉ định
    @Lob
    @Column(name = "vaccine_contraind", columnDefinition = "TEXT")
    private String vaccineContraindication;

    // Phản ứng sau tiêm
    @Lob
    @Column(name = "vaccine_reaction", columnDefinition = "TEXT")
    private String vaccineReaction;

    //So mui tre em
    @Column(name = "vaccine_child_dc")
    private int vaccineChildDoseCount;

    //So mui nguoi lon
    @Column(name = "vaccine_adult_dc")
    private int vaccineAdultDoseCount;

    // Bảo quản
    @Lob
    @Column(name = "vaccine_storage", columnDefinition = "TEXT")
    private String vaccineStorage;

    //Phac do tiem
    @Lob
    @Column(name = "vaccine_schedule", columnDefinition = "TEXT")
    private String vaccineInjectionSchedule;

    // Đối tượng
    @Lob
    @Column(name = "vaccine_patient", columnDefinition = "TEXT")
    private String vaccinePatient;

    @Column(name = "vaccine_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineCreateAt;

    @Column(name = "vaccine_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineUpdateAt;

    @ManyToOne
    @JoinColumn(name = "disease_id")
    private Disease disease;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<BatchDetail> batchDetailList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    @OneToMany(mappedBy = "vaccine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageDetail> packageDetailList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "vaccine", orphanRemoval = true)
    private List<PriceHistory> priceHistoryList = new ArrayList<>();
}
