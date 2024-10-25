package vcms.dto.request;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineCreationRequestByAdmin {
    private String vaccineName;

    private MultipartFile vaccineImageFile;

    private Set<String> vaccineAgeRange;

    private String vaccineDescription;

    // Nguồn gốc
    private String vaccineOrigin;

    // Đường tiêm
    private String vaccineInjectionRoute;

    // Chống chỉ định
    private String vaccineContraindication;

    // Phản ứng sau tiêm
    private String vaccineReaction;

    //So mui tre em
    private int vaccineChildDoseCount;

    //So mui nguoi lon
    private int vaccineAdultDoseCount;

    // Bảo quản
    private String vaccineStorage;

    //Phac do tiem
    private String vaccineInjectionSchedule;

    // Đối tượng
    private String vaccinePatient;

    private Long diseaseId;
}
