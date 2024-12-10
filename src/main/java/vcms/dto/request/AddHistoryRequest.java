package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddHistoryRequest {
    private String customerIdentifier;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String vaccineCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccinationRecordDate;

    private String vaccinationRecordDosage;

    private String vaccinationRecordDose;

    private String employeeUsername;
}
