package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerUpdateAt;

}
