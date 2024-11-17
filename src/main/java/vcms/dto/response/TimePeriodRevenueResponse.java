package vcms.dto.response;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimePeriodRevenueResponse {
    private String period;
    private Long revenue;
    private Long cost;
    private Long profit;
}
