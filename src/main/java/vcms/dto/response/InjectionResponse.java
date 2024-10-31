package vcms.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InjectionResponse {
    private String name;
    private int doseCount;
}
