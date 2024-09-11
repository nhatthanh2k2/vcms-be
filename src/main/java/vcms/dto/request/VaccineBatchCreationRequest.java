package vcms.dto.request;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineBatchCreationRequest {
    private String vaccineBatchNumber;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccineBatchImportDate;

    private int vaccineBatchQuantity;

    private int vaccineBatchValue;

    private MultipartFile batchDetailFile;
}
