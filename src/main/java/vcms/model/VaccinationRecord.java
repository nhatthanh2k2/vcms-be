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
    @Column(name = "vaccination_record_id")
    private Long vaccinationRecordId;

    // Ngày tiêm
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "vaccination_record_date")
    private LocalDate vaccinationRecordDate;

    // Liều lượng tiêm
    @Column(name = "vaccination_record_dosage")
    private String vaccinationRecordDosage;

    // Mũi tiêm
    @Column(name = "vaccination_record_shot")
    private String vaccinationRecordShot;

    // Người tiêm
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // Vắc-xin tiêm
    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    // nhân viên thực hiện
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
