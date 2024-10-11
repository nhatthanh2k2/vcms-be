package vcms.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    private String employeeUsername;

    private String employeeEmail;

    private String newPassword;
}
