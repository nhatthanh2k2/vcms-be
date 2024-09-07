package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "employees")
@JsonIgnoreProperties({"employeePassword"})
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq",
            allocationSize = 1, initialValue = 10000)
    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "employee_full_name")
    private String employeeFullName;

    @Column(name = "employee_gender")
    private Gender employeeGender;

    @Column(name = "employee_dob")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate employeeDob;

    @Column(name = "employee_email")
    private String employeeEmail;

    @Column(name = "employee_phone")
    private String employeePhone;

    @Column(name = "employee_avatar")
    private String employeeAvatar;

    @Column(name = "employee_province")
    private String employeeProvince;

    @Column(name = "employee_district")
    private String employeeDistrict;

    @Column(name = "employee_ward")
    private String employeeWard;

    @Column(name = "employee_qualification")
    private String employeeQualification;

    @Column(name = "employee_role")
    private Set<String> roles;

    @Column(name = "employee_username")
    private String employeeUsername;

    @Column(name = "employee_password")
    private String employeePassword;

    @Column(name = "employee_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeCreateAt;

    @Column(name = "empolyee_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeUpdateAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "employee", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

}
