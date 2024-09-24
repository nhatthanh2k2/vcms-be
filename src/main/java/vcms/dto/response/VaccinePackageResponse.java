package vcms.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinePackageResponse {

    private Long vaccinePackageId;

    private String vaccinePackageName;

    private int vaccinePackagePrice;

    private String vaccinePackageType;

}
