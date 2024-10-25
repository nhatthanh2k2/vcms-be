package vcms.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineBatchCreationRequest {
    private String vaccineBatchNumber;

    private int vaccineBatchQuantity;

    private BigInteger vaccineBatchValue;

    private MultipartFile batchDetailFile;
}
