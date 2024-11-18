package vcms.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Column(name = "screen_reccord_id")
    private Long screeningRecordId;

    @Column(name = "screen_reccord_code")
    private String screeningRecordCode;

    @Column(name = "screen_reccord_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate screeningRecordDate;

    @Column(name = "screen_reccord_height")
    private String screeningRecordHeight; // Chiều cao (m)

    @Column(name = "screen_reccord_weight")
    private String screeningRecordWeight; // Cân nặng (kg)

    @Column(name = "screen_reccord_blood_pressure")
    private String screeningRecordBloodPressure; // Huyết áp (mmHg)

    @Column(name = "screen_reccord_heart_rate")
    private String screeningRecordHeartRate; // Nhịp tim (bpm)

    @Column(name = "screen_reccord_temperature")
    private String screeningRecordTemperature; // Nhiệt độ cơ thể (°C)

    @Column(name = "screen_reccord_respiratory_rate")
    private String screeningRecordRespiratoryRate; // Tình trạng hô hấp

    @Column(name = "screen_reccord_chronic_diseases", length = 500)
    private String screeningRecordChronicDiseases; // Các bệnh mãn tính

    @Column(name = "screen_reccord_allergies", length = 500)
    private String screeningRecordAllergies; // Dị ứng

    @Column(name = "screen_reccord_medications", length = 500)
    private String screeningRecordCurrentMedications; // Thuốc đang sử dụng

    @Column(name = "screen_reccord_symptoms", length = 500)
    private String screeningRecordAbnormalSymptoms; // Các triệu chứng bất thường

    @Column(name = "screen_reccord_note")
    private String screeningRecordNotes; // Ghi chú bổ sung

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_reccord_result")
    private ScreeningResult screeningRecordResult; // Kết quả khám sàng lọc

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
