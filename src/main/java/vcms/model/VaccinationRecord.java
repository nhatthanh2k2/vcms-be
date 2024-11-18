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
    @Column(name = "vaccination_reccord_id")
    private Long vaccinationRecordId;

    @Column(name = "vaccination_reccord_code")
    private String vaccinationRecordCode;

    // Ngày tiêm
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "vaccination_reccord_date")
    private LocalDate vaccinationRecordDate;

    @Column(name = "vaccination_reccord_type")
    private String vaccinationRecordType;

    // Liều lượng tiêm
    @Column(name = "vaccination_reccord_dosage")
    private String vaccinationRecordDosage;

    // Mũi tiêm
    @Column(name = "vaccination_reccord_dose")
    private String vaccinationRecordDose;

    @Column(name = "vaccination_reccord_total")
    private int vaccinationRecordTotal;

    @Column(name = "vaccination_reccord_payment")
    private String vaccinationRecordPayment;

    @Column(name = "vaccination_reccord_receipt_source")
    private String vaccinationRecordReceiptSource;

    // Người tiêm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_package_id")
    private VaccinePackage vaccinePackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_batch_id")
    private VaccineBatch vaccineBatch;

    // nhân viên thực hiện
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
