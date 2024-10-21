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

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccinationRecordDate;

    private String vaccinationRecordType;

    private String vaccinationRecordDosage;

    private String vaccinationRecordDose;

    private int vaccinationRecordTotal;

    private String vaccinationRecordPayment;

    private CustomerResponse customerResponse;

    private VaccineResponse vaccineResponse;

    private VaccinePackageResponse vaccinePackageResponse;

    private VaccineBatchResponse vaccineBatchResponse;

    private EmployeeResponse employeeResponse;
}
