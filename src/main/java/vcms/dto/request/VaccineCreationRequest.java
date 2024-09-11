package vcms.dto.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VaccineCreationRequest {

    private String vaccineName;

    private String vaccineImage;

    //Cong dung
    private String vaccinePurpose;

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

}
