package vcms.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PackageDetailResponse {

    private Long packageDetailId;

    private VaccineResponse vaccineResponse;

    private DiseaseResponse diseaseResponse;

    private int packageDetailDoseCount;
}
