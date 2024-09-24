package vcms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vac_pkg_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinePackageDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vac_pkg_det_id")
    private Long vaccinePkgDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vac_pkg_id")
    private VaccinePackage vaccinePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @Column(name = "dose_count")
    private int doseCount;
}
