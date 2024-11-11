package vcms.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;


@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewsCreationRequest {
    private String newsTitle;

    private String newsContent;

    private MultipartFile newsImage;

    private Long employeeId;

}
