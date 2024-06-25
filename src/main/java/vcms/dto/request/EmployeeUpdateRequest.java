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
public class EmployeeUpdateRequest {
    private String employeeFullName;
    private Gender employeeGender;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate employeeDob;
    private String employeeEmail;
    private String employeePhone;
    private int employeeProvince;
    private int employeeDistrict;
    private int employeeWard;
    private String employeeDegree;
    private String employeeQualification;

}
