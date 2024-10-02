package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.InjectionType;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentWithCustomerCodeRequest {

    private String appointmentCustomerCode;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentInjectionDate;

    private InjectionType apppointmentInjectionType;

    private Long appointmentBatchDetailId;

    private Long appointmentVaccinePackageId;
}
