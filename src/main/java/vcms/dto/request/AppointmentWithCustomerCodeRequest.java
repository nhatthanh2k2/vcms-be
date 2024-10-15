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

    private String customerIdentifier;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate injectionDate;

    private InjectionType injectionType;

    private Long batchDetailId;

    private Long vaccinePackageId;
}
