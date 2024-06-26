package vcms.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntrospectResponse {
    private boolean valid;
}
