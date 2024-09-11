package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineBatchResponse {
    private Long vaccineBatchId;

    private String vaccineBatchNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccineBatchImportDate;

    private int vaccineBatchQuantity;

    private int vaccineBatchValue;
}
