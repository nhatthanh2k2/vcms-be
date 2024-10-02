package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long employeeId;

    private String employeeFullName;

    private Gender employeeGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate employeeDob;

    private String employeeEmail;

    private String employeePhone;

    private String employeeAvatar;

    private String employeeProvince;

    private String employeeDistrict;

    private String employeeWard;

    private String employeeQualification;

    private String employeePosition;

    private Set<String> roles;

    private String employeeUsername;

}
