package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.RecordStatus;
import vcms.enums.ScreeningResult;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScreeningRecordResponse {
    private Long screeningRecordId;

    private String screeningRecordCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate screeningRecordDate;

    private RecordStatus screeningRecordStatus;

    private String screeningRecordHeight; // Chiều cao (m)

    private String screeningRecordWeight; // Cân nặng (kg)

    private String screeningRecordBloodPressure; // Huyết áp (mmHg)

    private String screeningRecordHeartRate; // Nhịp tim (bpm)

    private String screeningRecordTemperature; // Nhiệt độ cơ thể (°C)

    private String screeningRecordRespiratoryRate; // Tình trạng hô hấp

    private String screeningRecordChronicDiseases; // Các bệnh mãn tính

    private String screeningRecordAllergies; // Dị ứng

    private String screeningRecordCurrentMedications; // Thuốc đang sử dụng

    private String screeningRecordAbnormalSymptoms; // Các triệu chứng bất thường

    private String screeningRecordNotes; // Ghi chú bổ sung

    private ScreeningResult screeningRecordResult; // Kết quả khám sàng lọc

    private CustomerResponse customerResponse;

    private EmployeeResponse employeeResponse;
}
