package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    @Column(name = "vaccine_package_id")
    private Long vaccinePackageId;

    @Column(name = "vaccine_package_name")
    private String vaccinePackageName;

    @Column(name = "vaccine_package_price")
    private int vaccinePackagePrice;

    @Column(name = "vaccine_package_type")
    private String vaccinePackageType;

    @Column(name = "vaccine_package_custom")
    private int isCustomPackage;

    @Column(name = "vaccine_package_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccinePackageCreateAt;

    @Column(name = "vaccine_package_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccinePackageUpdateAt;

    @OneToMany(mappedBy = "vaccinePackage", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PackageDetail> packageDetailList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    @JsonManagedReference
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    @JsonManagedReference
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

}
