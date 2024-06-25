package vcms.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import vcms.enums.Gender;

import java.time.LocalDate;

public class CustomerRequest {

    private String customerFullName;

    private Gender customerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate customerDob;

    private String customerEmail;

    private String customerPhone;

    private int customerProvince;

    private int customerDistrict;

    private int customerWard;

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

}
