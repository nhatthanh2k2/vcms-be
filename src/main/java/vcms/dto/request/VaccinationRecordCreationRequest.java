package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationRecordCreationRequest {

    private String customerPhone;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String employeeUsername;

    private Long vaccineId;

    private Long vaccinePackageId;

    private Long vaccineBatchId;

    private String vaccinationRecordType;

    private String vaccinationRecordDosage;

    private String vaccinationRecordDose;

    private int vaccinationRecordTotal;

    private String vaccinationRecordPayment;

    private String vaccinationRecordReceiptSource;
}
