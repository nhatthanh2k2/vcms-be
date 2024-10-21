package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "vaccination_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vac_rec_id")
    private Long vaccinationRecordId;

    @Column(name = "vac_rec_code")
    private String vaccinationRecordCode;

    // Ngày tiêm
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "vac_rec_date")
    private LocalDate vaccinationRecordDate;

    @Column(name = "vac_rec_type")
    private String vaccinationRecordType;

    // Liều lượng tiêm
    @Column(name = "vac_rec_dosage")
    private String vaccinationRecordDosage;

    // Mũi tiêm
    @Column(name = "vac_rec_dose")
    private String vaccinationRecordDose;

    @Column(name = "vac_rec_total")
    private int vaccinationRecordTotal;

    @Column(name = "vac_rec_payment")
    private String vaccinationRecordPayment;

    // Người tiêm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vac_pkg_id")
    private VaccinePackage vaccinePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vac_batch_id")
    private VaccineBatch vaccineBatch;

    // nhân viên thực hiện
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
