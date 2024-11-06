package vcms.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateVaccinePriceRequest {
    private Long batchDetailId;

    private int newPrice;
}
