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
            allocationSize = 1, initialValue = 1000)
    @Column(name = "emp_id")
    private Long employeeId;

    @Column(name = "emp_full_name")
    private String employeeFullName;

    @Column(name = "emp_gender")
    private Gender employeeGender;

    @Column(name = "emp_dob")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate employeeDob;

    @Column(name = "emp_email")
    private String employeeEmail;

    @Column(name = "emp_phone")
    private String employeePhone;

    @Column(name = "emp_avatar")
    private String employeeAvatar;

    @Column(name = "emp_province")
    private String employeeProvince;

    @Column(name = "emp_district")
    private String employeeDistrict;

    @Column(name = "emp_ward")
    private String employeeWard;

    @Column(name = "emp_qual")
    private String employeeQualification;

    @Column(name = "emp_position")
    private String employeePosition;

    @Column(name = "emp_role")
    private Set<String> roles;

    @Column(name = "emp_username")
    private String employeeUsername;

    @Column(name = "emp_password")
    private String employeePassword;

    @Column(name = "emp_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeCreateAt;

    @Column(name = "emp_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeUpdateAt;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL,
            mappedBy = "employee", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

}
