package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCreationRequest {

    private String employeeFullName;

    private Gender employeeGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate employeeDob;

    private String employeeEmail;

    private String employeePhone;

    private String employeeProvince;

    private String employeeDistrict;

    private String employeeWard;

    private String employeeQualification;

    private String employeePosition;

    private Set<String> roles;

}
