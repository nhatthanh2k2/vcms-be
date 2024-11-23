package vcms.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.RecordStatus;
import vcms.enums.ScreeningResult;

import java.time.LocalDate;


@Entity
@Table(name = "screening_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sr_id")
    private Long screeningRecordId;

    @Column(name = "sr_code")
    private String screeningRecordCode;

    @Column(name = "sr_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate screeningRecordDate;

    @Column(name = "sr_status")
    private RecordStatus screeningRecordStatus;

    @Column(name = "sr_height")
    private String screeningRecordHeight; // Chiều cao (m)

    @Column(name = "sr_weight")
    private String screeningRecordWeight; // Cân nặng (kg)

    @Column(name = "sr_blood_pressure")
    private String screeningRecordBloodPressure; // Huyết áp (mmHg)

    @Column(name = "sr_heart_rate")
    private String screeningRecordHeartRate; // Nhịp tim (bpm)

    @Column(name = "sr_temperature")
    private String screeningRecordTemperature; // Nhiệt độ cơ thể (°C)

    @Column(name = "sr_respiratory_rate")
    private String screeningRecordRespiratoryRate; // Tình trạng hô hấp

    @Column(name = "sr_diseases", length = 500)
    private String screeningRecordChronicDiseases; // Các bệnh mãn tính

    @Column(name = "sr_allergies", length = 500)
    private String screeningRecordAllergies; // Dị ứng

    @Column(name = "sr_medications", length = 500)
    private String screeningRecordCurrentMedications; // Thuốc đang sử dụng

    @Column(name = "sr_symptoms", length = 500)
    private String screeningRecordAbnormalSymptoms; // Các triệu chứng bất thường

    @Column(name = "sr_note")
    private String screeningRecordNotes; // Ghi chú bổ sung

    @Enumerated(EnumType.STRING)
    @Column(name = "sr_result")
    private ScreeningResult screeningRecordResult; // Kết quả khám sàng lọc

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}
