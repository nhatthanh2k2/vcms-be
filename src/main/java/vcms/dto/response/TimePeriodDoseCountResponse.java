package vcms.dto.response;


import lombok.*;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodDoseCountResponse {
    private String period;
    private Long doseCount;
}
