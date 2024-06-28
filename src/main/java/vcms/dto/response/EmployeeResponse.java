package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private int employeeProvince;

    private int employeeDistrict;

    private int employeeWard;

    private String employeeDegree;

    private String employeeQualification;

    private Set<String> roles;

    private String employeeUsername;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeUpdateAt;

}
