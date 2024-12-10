package vcms.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryRequest {
    private Long vaccineId;

    private Integer oldPrice;

    private Integer newPrice;
}
