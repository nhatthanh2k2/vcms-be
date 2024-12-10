package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PriceHistoryResponse {
    private Long priceHistoryId;

    private Integer priceHistoryOldPrice;

    private Integer priceHistoryNewPrice;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime priceHistoryUpdateTime;
}
