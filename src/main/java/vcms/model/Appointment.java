package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import vcms.enums.Gender;

import java.time.LocalDate;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Long appointmentId;

    // thong tin nguoi tiêm
    @Column(name = "appointment_customer_code")
    private String appointmentCustomerCode;

    @Column(name = "appointment_full_name")
    private String appointmentFullName;

    @Column(name = "appointment_dob")
    private LocalDate appointmentDob;

    @Column(name = "appointment_gender")
    private Gender appointmentGender;

    @Column(name = "appointment_phone")
    private String appointmentPhone;

    @Column(name = "appointment_email")
    private String appointmentEmail;

    @Column(name = "appointment_province")
    private int appointmentProvince;

    @Column(name = "appointment_district")
    private int appointmentDistrict;

    @Column(name = "appointment_ward")
    private int appointmentWard;

    @Column(name = "appointment_injection_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentInjectionDate;

    // thong tin lien he
    @Column(name = "appointment_relatives_full_name")
    private String appointmentRelativesFullName;

    @Column(name = "appointment_relatives_phone")
    private String appointmentRelativesPhone;

    @Column(name = "appointment_relatives_relationship")
    private String appointmentRelativesRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    @JsonBackReference
    private Vaccine vaccine;

    @ManyToOne
    @JoinColumn(name = "vaccine_package_id")
    @JsonBackReference
    private VaccinePackage vaccinePackage;

    public Appointment() {
    }

    public Appointment(Long appointmentId, String appointmentCustomerCode,
                       String appointmentFullName, LocalDate appointmentDob,
                       Gender appointmentGender, String appointmentPhone,
                       String appointmentEmail, int appointmentProvince,
                       int appointmentDistrict, int appointmentWard,
                       LocalDate appointmentInjectionDate,
                       String appointmentRelativesFullName,
                       String appointmentRelativesPhone,
                       String appointmentRelativesRelationship,
                       Customer customer,
                       Vaccine vaccine, VaccinePackage vaccinePackage) {
        this.appointmentId = appointmentId;
        this.appointmentCustomerCode = appointmentCustomerCode;
        this.appointmentFullName = appointmentFullName;
        this.appointmentDob = appointmentDob;
        this.appointmentGender = appointmentGender;
        this.appointmentPhone = appointmentPhone;
        this.appointmentEmail = appointmentEmail;
        this.appointmentProvince = appointmentProvince;
        this.appointmentDistrict = appointmentDistrict;
        this.appointmentWard = appointmentWard;
        this.appointmentInjectionDate = appointmentInjectionDate;
        this.appointmentRelativesFullName = appointmentRelativesFullName;
        this.appointmentRelativesPhone = appointmentRelativesPhone;
        this.appointmentRelativesRelationship = appointmentRelativesRelationship;
        this.customer = customer;
        this.vaccine = vaccine;
        this.vaccinePackage = vaccinePackage;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getAppointmentCustomerCode() {
        return appointmentCustomerCode;
    }

    public void setAppointmentCustomerCode(String appointmentCustomerCode) {
        this.appointmentCustomerCode = appointmentCustomerCode;
    }

    public String getAppointmentFullName() {
        return appointmentFullName;
    }

    public void setAppointmentFullName(String appointmentFullName) {
        this.appointmentFullName = appointmentFullName;
    }

    public LocalDate getAppointmentDob() {
        return appointmentDob;
    }

    public void setAppointmentDob(LocalDate appointmentDob) {
        this.appointmentDob = appointmentDob;
    }

    public Gender getAppointmentGender() {
        return appointmentGender;
    }

    public void setAppointmentGender(Gender appointmentGender) {
        this.appointmentGender = appointmentGender;
    }

    public String getAppointmentPhone() {
        return appointmentPhone;
    }

    public void setAppointmentPhone(String appointmentPhone) {
        this.appointmentPhone = appointmentPhone;
    }

    public String getAppointmentEmail() {
        return appointmentEmail;
    }

    public void setAppointmentEmail(String appointmentEmail) {
        this.appointmentEmail = appointmentEmail;
    }

    public int getAppointmentProvince() {
        return appointmentProvince;
    }

    public void setAppointmentProvince(int appointmentProvince) {
        this.appointmentProvince = appointmentProvince;
    }

    public int getAppointmentDistrict() {
        return appointmentDistrict;
    }

    public void setAppointmentDistrict(int appointmentDistrict) {
        this.appointmentDistrict = appointmentDistrict;
    }

    public int getAppointmentWard() {
        return appointmentWard;
    }

    public void setAppointmentWard(int appointmentWard) {
        this.appointmentWard = appointmentWard;
    }

    public LocalDate getAppointmentInjectionDate() {
        return appointmentInjectionDate;
    }

    public void setAppointmentInjectionDate(
            LocalDate appointmentInjectionDate) {
        this.appointmentInjectionDate = appointmentInjectionDate;
    }

    public String getAppointmentRelativesFullName() {
        return appointmentRelativesFullName;
    }

    public void setAppointmentRelativesFullName(
            String appointmentRelativesFullName) {
        this.appointmentRelativesFullName = appointmentRelativesFullName;
    }

    public String getAppointmentRelativesPhone() {
        return appointmentRelativesPhone;
    }

    public void setAppointmentRelativesPhone(String appointmentRelativesPhone) {
        this.appointmentRelativesPhone = appointmentRelativesPhone;
    }

    public String getAppointmentRelativesRelationship() {
        return appointmentRelativesRelationship;
    }

    public void setAppointmentRelativesRelationship(
            String appointmentRelativesRelationship) {
        this.appointmentRelativesRelationship = appointmentRelativesRelationship;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }

    public VaccinePackage getVaccinePackage() {
        return vaccinePackage;
    }

    public void setVaccinePackage(VaccinePackage vaccinePackage) {
        this.vaccinePackage = vaccinePackage;
    }
}
