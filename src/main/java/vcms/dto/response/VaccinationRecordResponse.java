package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationRecordResponse {
    private Long vaccinationRecordId;

    private String vaccinationRecordCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccinationRecordDate;

    private String vaccinationRecordType;

    private String vaccinationRecordDosage;

    private String vaccinationRecordDose;

    private int vaccinationRecordTotal;

    private String vaccinationRecordPayment;

    private String vaccinationRecordReceiptSource;

    private CustomerResponse customerResponse;

    private String vaccineName;

    private String vaccinePackageName;

    private String vaccineBatchNumber;

    private EmployeeResponse employeeResponse;
}
