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

@Entity
@Table(name = "vaccine_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vp_id")
    private Long vaccinePackageId;

    @Column(name = "vp_name")
    private String vaccinePackageName;

    @Column(name = "vp_price")
    private int vaccinePackagePrice;

    @Column(name = "vp_type")
    private String vaccinePackageType;

    @Column(name = "vp_custom")
    private int isCustomPackage;

    @Column(name = "vp_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccinePackageCreateAt;

    @Column(name = "vp_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccinePackageUpdateAt;

    @OneToMany(mappedBy = "vaccinePackage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageDetail> packageDetailList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

}
