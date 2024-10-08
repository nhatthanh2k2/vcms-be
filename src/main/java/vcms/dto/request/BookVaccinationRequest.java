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
public class BookVaccinationRequest {
    private String customerIdentifier;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String actionType;

    private InjectionType injectionType;

    private String payment;

    private Long batchDetailSelected;

    private Long packageSelected;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate injectionDate;
}
