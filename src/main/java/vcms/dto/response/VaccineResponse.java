package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VaccineResponse {
    private Long vaccineId;

    private String vaccineCode;

    private String vaccineName;

    private String vaccineImage;

    private String vaccineDescription;

    // Nguồn gốc
    private String vaccineOrigin;


    // Đường tiêm
    private String vaccineInjectionRoute;

    // Chống chỉ định
    private String vaccineContraindication;

    // Tương tác thuốc
    private String vaccineDrugInteractions;

    // Tác dụng không mong muốn
    private String vaccineAdverseEffects;

    // Bảo quản
    private String vaccineStorage;

    // Đối tượng
    private String vaccinePatient;

    // Phản ứng sau tiêm
    private String vaccineReaction;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime vaccineUpdateAt;
}
