package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.Gender;

import java.time.LocalDate;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
