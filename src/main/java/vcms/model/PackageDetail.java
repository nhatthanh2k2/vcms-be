package vcms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "package_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pkg_det_id")
    private Long packageDetailId;

    @Column(name = "pkg_det_dose_count")
    private int packageDetailDoseCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_package_id")
    private VaccinePackage vaccinePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;


}
