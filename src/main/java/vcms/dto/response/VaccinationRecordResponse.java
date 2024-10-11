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

    private String vaccinationRecordDosage;

    private String vaccinationRecordDose;

    private String vaccinationRecordNote;

    private BatchDetailResponse batchDetailResponse;

    private VaccinePackageResponse vaccinePackageResponse;
}
