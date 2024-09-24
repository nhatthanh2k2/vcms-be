package vcms.dto.request;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogoutRequest {
    private String token;
}
