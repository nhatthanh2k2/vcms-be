package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {
    private Long customerId;

    private String customerCode;

    private String customerFullName;

    private Gender customerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String customerEmail;

    private String customerPhone;

    private String customerProvince;

    private String customerDistrict;

    private String customerWard;


}
