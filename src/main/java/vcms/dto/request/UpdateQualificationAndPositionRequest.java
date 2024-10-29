package vcms.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQualificationAndPositionRequest {
    private String employeeUsername;

    private String employeePosition;

    private String employeeQualification;
}
