package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseResponse {
    private Long diseaseId;

    private String diseaseName;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime diseaseCreateAt;

}
