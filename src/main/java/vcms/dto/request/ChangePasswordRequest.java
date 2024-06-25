package vcms.dto.request;


import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {
    private String employeeUsername;
    private String employeePassword;
    private String newPassword;
}
