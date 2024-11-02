package vcms.dto.request;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinePackageUpdateRequest {
    private Long vaccinePackageId;

    private String vaccinePackageName;

    private int vaccinePackagePrice;

    private String vaccinePackageType;

    private List<Long> vaccineIdList;

    private List<Integer> doseCountList;
}
