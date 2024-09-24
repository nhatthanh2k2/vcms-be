package vcms.dto.request;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinePackageCreationRequest {

    private String vaccinePackageName;

    private String vaccinePackageType;

    private List<Long> vaccineIdList;

    private List<Integer> doseCountList;
}
