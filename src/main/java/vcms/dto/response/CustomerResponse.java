package vcms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerResponse {
    private Long customerId;

    private String customerCode;

    private String customerFullName;

    private Gender customerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String customerEmail;

    private String customerPhone;

    private int customerProvince;

    private int customerDistrict;

    private int customerWard;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerCreateAt;

    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerUpdateAt;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getCustomerFullName() {
        return customerFullName;
    }

    public void setCustomerFullName(String customerFullName) {
        this.customerFullName = customerFullName;
    }

    public Gender getCustomerGender() {
        return customerGender;
    }

    public void setCustomerGender(Gender customerGender) {
        this.customerGender = customerGender;
    }

    public LocalDate getCustomerDob() {
        return customerDob;
    }

    public void setCustomerDob(LocalDate customerDob) {
        this.customerDob = customerDob;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getCustomerProvince() {
        return customerProvince;
    }

    public void setCustomerProvince(int customerProvince) {
        this.customerProvince = customerProvince;
    }

    public int getCustomerDistrict() {
        return customerDistrict;
    }

    public void setCustomerDistrict(int customerDistrict) {
        this.customerDistrict = customerDistrict;
    }

    public int getCustomerWard() {
        return customerWard;
    }

    public void setCustomerWard(int customerWard) {
        this.customerWard = customerWard;
    }

    public LocalDateTime getCustomerCreateAt() {
        return customerCreateAt;
    }

    public void setCustomerCreateAt(LocalDateTime customerCreateAt) {
        this.customerCreateAt = customerCreateAt;
    }

    public LocalDateTime getCustomerUpdateAt() {
        return customerUpdateAt;
    }

    public void setCustomerUpdateAt(LocalDateTime customerUpdateAt) {
        this.customerUpdateAt = customerUpdateAt;
    }
}
