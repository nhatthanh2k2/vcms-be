package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreationRequest {

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

    private String appointmentRelativesFullName;

    private String appointmentRelativesPhone;

    private String appointmentRelativesRelationship;

    private Long appointmentBatchDetailId;
}
