package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import vcms.enums.Gender;
import vcms.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@JsonIgnoreProperties({"employeePassword"})
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
    private int employeeProvince;

    @Column(name = "employee_district")
    private int employeeDistrict;

    @Column(name = "employee_ward")
    private int employeeWard;

    @Column(name = "employee_degree")
    private String employeeDegree;

    @Column(name = "employee_qualification")
    private String employeeQualification;

    @Column(name = "employee_role")
    private Role employeeRole;

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

    public Employee() {
    }

    public Employee(String employeeFullName, Gender employeeGender,
                    LocalDate employeeDob, String employeeEmail,
                    String employeePhone, String employeeAvatar,
                    int employeeProvince, int employeeDistrict,
                    int employeeWard,
                    String employeeDegree, String employeeQualification,
                    Role employeeRole, String employeeUsername,
                    String employeePassword, LocalDateTime employeeCreateAt,
                    LocalDateTime employeeUpdateAt) {
        this.employeeFullName = employeeFullName;
        this.employeeGender = employeeGender;
        this.employeeDob = employeeDob;
        this.employeeEmail = employeeEmail;
        this.employeePhone = employeePhone;
        this.employeeAvatar = employeeAvatar;
        this.employeeProvince = employeeProvince;
        this.employeeDistrict = employeeDistrict;
        this.employeeWard = employeeWard;
        this.employeeDegree = employeeDegree;
        this.employeeQualification = employeeQualification;
        this.employeeRole = employeeRole;
        this.employeeUsername = employeeUsername;
        this.employeePassword = employeePassword;
        this.employeeCreateAt = employeeCreateAt;
        this.employeeUpdateAt = employeeUpdateAt;
    }

    public Employee(Long employeeId, String employeeFullName,
                    Gender employeeGender,
                    LocalDate employeeDob, String employeeEmail,
                    String employeePhone, String employeeAvatar,
                    int employeeProvince, int employeeDistrict,
                    int employeeWard,
                    String employeeDegree, String employeeQualification,
                    Role employeeRole, String employeeUsername,
                    String employeePassword, LocalDateTime employeeCreateAt,
                    LocalDateTime employeeUpdateAt,
                    List<VaccinationRecord> vaccinationRecordList) {
        this.employeeId = employeeId;
        this.employeeFullName = employeeFullName;
        this.employeeGender = employeeGender;
        this.employeeDob = employeeDob;
        this.employeeEmail = employeeEmail;
        this.employeePhone = employeePhone;
        this.employeeAvatar = employeeAvatar;
        this.employeeProvince = employeeProvince;
        this.employeeDistrict = employeeDistrict;
        this.employeeWard = employeeWard;
        this.employeeDegree = employeeDegree;
        this.employeeQualification = employeeQualification;
        this.employeeRole = employeeRole;
        this.employeeUsername = employeeUsername;
        this.employeePassword = employeePassword;
        this.employeeCreateAt = employeeCreateAt;
        this.employeeUpdateAt = employeeUpdateAt;
        this.vaccinationRecordList = vaccinationRecordList;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeFullName() {
        return employeeFullName;
    }

    public void setEmployeeFullName(String employeeFullName) {
        this.employeeFullName = employeeFullName;
    }

    public Gender getEmployeeGender() {
        return employeeGender;
    }

    public void setEmployeeGender(Gender employeeGender) {
        this.employeeGender = employeeGender;
    }

    public LocalDate getEmployeeDob() {
        return employeeDob;
    }

    public void setEmployeeDob(LocalDate employeeDob) {
        this.employeeDob = employeeDob;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeAvatar() {
        return employeeAvatar;
    }

    public void setEmployeeAvatar(String employeeAvatar) {
        this.employeeAvatar = employeeAvatar;
    }

    public int getEmployeeProvince() {
        return employeeProvince;
    }

    public void setEmployeeProvince(int employeeProvince) {
        this.employeeProvince = employeeProvince;
    }

    public int getEmployeeDistrict() {
        return employeeDistrict;
    }

    public void setEmployeeDistrict(int employeeDistrict) {
        this.employeeDistrict = employeeDistrict;
    }

    public int getEmployeeWard() {
        return employeeWard;
    }

    public void setEmployeeWard(int employeeWard) {
        this.employeeWard = employeeWard;
    }

    public String getEmployeeDegree() {
        return employeeDegree;
    }

    public void setEmployeeDegree(String employeeDegree) {
        this.employeeDegree = employeeDegree;
    }

    public String getEmployeeQualification() {
        return employeeQualification;
    }

    public void setEmployeeQualification(String employeeQualification) {
        this.employeeQualification = employeeQualification;
    }

    public Role getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(Role employeeRole) {
        this.employeeRole = employeeRole;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public String getEmployeePassword() {
        return employeePassword;
    }

    public void setEmployeePassword(String employeePassword) {
        this.employeePassword = employeePassword;
    }

    public List<VaccinationRecord> getVaccinationRecordList() {
        return vaccinationRecordList;
    }

    public void setVaccinationRecordList(
            List<VaccinationRecord> vaccinationRecordList) {
        this.vaccinationRecordList = vaccinationRecordList;
    }

    public LocalDateTime getEmployeeCreateAt() {
        return employeeCreateAt;
    }

    public void setEmployeeCreateAt(LocalDateTime employeeCreateAt) {
        this.employeeCreateAt = employeeCreateAt;
    }

    public LocalDateTime getEmployeeUpdateAt() {
        return employeeUpdateAt;
    }

    public void setEmployeeUpdateAt(LocalDateTime employeeUpdateAt) {
        this.employeeUpdateAt = employeeUpdateAt;
    }
}
