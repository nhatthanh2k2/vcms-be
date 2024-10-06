package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.AppointmentStatus;
import vcms.enums.Gender;
import vcms.enums.InjectionType;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentResponse {
    private Long appointmentId;

    private String appointmentCustomerFullName;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentCustomerDob;

    private Gender appointmentCustomerGender;

    private String appointmentCustomerPhone;

    private String appointmentCustomerEmail;

    private String appointmentCustomerProvince;

    private String appointmentCustomerDistrict;

    private String appointmentCustomerWard;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentInjectionDate;

    private AppointmentStatus appointmentStatus;

    private InjectionType appointmentInjectionType;

    private String appointmentRelativesFullName;

    private String appointmentRelativesPhone;

    private String appointmentRelativesRelationship;

    private BatchDetailResponse batchDetailResponse;

    private VaccinePackageResponse vaccinePackageResponse;
}
