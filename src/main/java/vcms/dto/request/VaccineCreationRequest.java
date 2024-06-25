package vcms.dto.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class VaccineCreationRequest {

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

}
