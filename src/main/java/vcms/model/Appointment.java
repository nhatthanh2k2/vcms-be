package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.AppointmentStatus;
import vcms.enums.Gender;
import vcms.enums.InjectionType;

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
    @Column(name = "appt_id")
    private Long appointmentId;

    // thong tin nguoi tiêm

    @Column(name = "appt_customer_name")
    private String appointmentCustomerFullName;

    @Column(name = "appt_customer_dob")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentCustomerDob;

    @Column(name = "appt_customer_gender")
    private Gender appointmentCustomerGender;

    @Column(name = "appt_customer_phone")
    private String appointmentCustomerPhone;

    @Column(name = "appt_customer_email")
    private String appointmentCustomerEmail;

    @Column(name = "appt_customer_province")
    private String appointmentCustomerProvince;

    @Column(name = "appt_customer_district")
    private String appointmentCustomerDistrict;

    @Column(name = "appt_customer ward")
    private String appointmentCustomerWard;

    @Column(name = "appt_injection_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentInjectionDate;

    @Column(name = "appt_status")
    @Enumerated(EnumType.STRING)
    private AppointmentStatus appointmentStatus;

    @Column(name = "appt_injection_type")
    @Enumerated(EnumType.STRING)
    private InjectionType appointmentInjectionType;

    // thong tin lien he
    @Column(name = "appt_relatives_name")
    private String appointmentRelativesFullName;

    @Column(name = "appt_relatives_phone")
    private String appointmentRelativesPhone;

    @Column(name = "appt_relatives_relationship")
    private String appointmentRelativesRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;

    @ManyToOne
    @JoinColumn(name = "vaccine_package_id")
    private VaccinePackage vaccinePackage;

}
