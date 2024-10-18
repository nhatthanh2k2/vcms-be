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
    @Column(name = "screen_rec_id")
    private Long screeningRecordId;

    private String screeningRecordCode;

    @Column(name = "screen_rec_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate screeningRecordDate;

    @Column(name = "screen_rec_height")
    private String screeningRecordHeight; // Chiều cao (m)

    @Column(name = "screen_rec_weight")
    private String screeningRecordWeight; // Cân nặng (kg)

    @Column(name = "screen_rec_bp")
    private String screeningRecordBloodPressure; // Huyết áp (mmHg)

    @Column(name = "screen_rec_hr")
    private String screeningRecordHeartRate; // Nhịp tim (bpm)

    @Column(name = "screen_rec_temp")
    private String screeningRecordTemperature; // Nhiệt độ cơ thể (°C)

    @Column(name = "screen_rec_rr")
    private String screeningRecordRespiratoryRate; // Tình trạng hô hấp

    @Column(name = "screen_rec_cd", length = 500)
    private String screeningRecordChronicDiseases; // Các bệnh mãn tính

    @Column(name = "screen_rec_allerg", length = 500)
    private String screeningRecordAllergies; // Dị ứng

    @Column(name = "screen_rec_med", length = 500)
    private String screeningRecordCurrentMedications; // Thuốc đang sử dụng

    @Column(name = "screen_rec_sympt", length = 500)
    private String screeningRecordAbnormalSymptoms; // Các triệu chứng bất thường

    @Column(name = "screen_rec_note")
    private String screeningRecordNotes; // Ghi chú bổ sung

    @Enumerated(EnumType.STRING)
    @Column(name = "screen_rec_result")
    private ScreeningResult screeningRecordResult; // Kết quả khám sàng lọc

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
