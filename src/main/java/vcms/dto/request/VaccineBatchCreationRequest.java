package vcms.dto.request;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineBatchCreationRequest {
    private String vaccineBatchNumber;

    private MultipartFile batchDetailFile;
}
