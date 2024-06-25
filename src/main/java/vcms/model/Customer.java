package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq",
            allocationSize = 1, initialValue = 10000)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_full_name")
    private String customerFullName;

    @Column(name = "customer_gender")
    private Gender customerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "customer_dob")
    private LocalDate customerDob;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_province")
    private int customerProvince;

    @Column(name = "customer_district")
    private int customerDistrict;

    @Column(name = "customer_ward")
    private int customerWard;

    @Column(name = "customer_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerCreateAt;

    @Column(name = "customer_updateAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerUpdateAt;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Relatives relatives;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<Appointment> appointmentList = new ArrayList<>();

    public Customer() {
    }

    public Customer(Long customerId, String customerCode,
                    String customerFullName,
                    Gender customerGender, LocalDate customerDob,
                    String customerEmail, String customerPhone,
                    int customerProvince, int customerDistrict,
                    int customerWard,
                    Relatives relatives, LocalDateTime customerCreateAt,
                    LocalDateTime customerUpdateAt,
                    List<VaccinationRecord> vaccinationRecordList,
                    List<Order> orderList, List<Appointment> appointmentList) {
        this.customerId = customerId;
        this.customerCode = customerCode;
        this.customerFullName = customerFullName;
        this.customerGender = customerGender;
        this.customerDob = customerDob;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.customerProvince = customerProvince;
        this.customerDistrict = customerDistrict;
        this.customerWard = customerWard;
        this.relatives = relatives;
        this.customerCreateAt = customerCreateAt;
        this.customerUpdateAt = customerUpdateAt;
        this.vaccinationRecordList = vaccinationRecordList;
        this.orderList = orderList;
        this.appointmentList = appointmentList;
    }

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

    public Relatives getRelatives() {
        return relatives;
    }

    public void setRelatives(Relatives relatives) {
        this.relatives = relatives;
    }

    public List<VaccinationRecord> getVaccinationRecordList() {
        return vaccinationRecordList;
    }

    public void setVaccinationRecordList(
            List<VaccinationRecord> vaccinationRecordList) {
        this.vaccinationRecordList = vaccinationRecordList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(
            List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
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
