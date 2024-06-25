package vcms.dto.request;

import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class IntrospectRequest {
    private String token;
}
