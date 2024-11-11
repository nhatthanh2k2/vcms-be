package vcms.dto.request;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsUpdateRequest {
    private Long newsId;

    private String status;
}
