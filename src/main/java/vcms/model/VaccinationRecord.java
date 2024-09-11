package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    // Ngày tiêm
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "vac_rec_date")
    private LocalDate vaccinationRecordDate;

    // Liều lượng tiêm
    @Column(name = "vac_rec_dosage")
    private String vaccinationRecordDosage;

    // Mũi tiêm
    @Column(name = "vac_rec_dose")
    private String vaccinationRecordDose;

    @Column(name = "vac_rec_note")
    private String vaccinationRecordNote;

    // Người tiêm
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Vắc-xin tiêm
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_detail_id")
    @JsonBackReference
    private BatchDetail batchDetail;

    // nhân viên thực hiện
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
