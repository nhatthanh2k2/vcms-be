package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import vcms.enums.Gender;

import java.time.LocalDate;


public class EmployeeUpdateRequest {
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
}
