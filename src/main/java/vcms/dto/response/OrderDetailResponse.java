package vcms.dto.response;

import lombok.*;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private Long orderDetailId;

    private BatchDetailResponse batchDetailResponse;

    private VaccinePackageResponse vaccinePackageResponse;
}
