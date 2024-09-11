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

    private List<Long> vaccineIdList;
}
