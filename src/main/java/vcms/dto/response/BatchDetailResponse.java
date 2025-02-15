package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BatchDetailResponse {
    private Long batchDetailId;

    private Integer batchDetailRemainingQuantity;

    private Integer batchDetailInitialQuantity;

    private int batchDetailVaccinePrice;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailManufactureDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailExpirationDate;

    private VaccineResponse vaccineResponse;

    private DiseaseResponse diseaseResponse;

}
