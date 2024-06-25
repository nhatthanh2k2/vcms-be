package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import vcms.enums.Gender;
import vcms.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    private Role employeeRole;

    private String employeeUsername;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime employeeUpdateAt;

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
