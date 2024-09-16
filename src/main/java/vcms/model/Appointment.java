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
    @Column(name = "appt_id")
    private Long appointmentId;

    // thong tin nguoi tiêm

    @Column(name = "appt_cust_full_name")
    private String appointmentCustomerFullName;

    @Column(name = "appt_cust_dob")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentCustomerDob;

    @Column(name = "appt_cust_gender")
    private Gender appointmentCustomerGender;

    @Column(name = "appt_cust_phone")
    private String appointmentCustomerPhone;

    @Column(name = "appt_cust_email")
    private String appointmentCustomerEmail;

    @Column(name = "appt_cust_province")
    private String appointmentCustomerProvince;

    @Column(name = "appt_cust_district")
    private String appointmentCustomerDistrict;

    @Column(name = "appt_cust ward")
    private String appointmentCustomerWard;

    @Column(name = "appt_inj_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate appointmentInjectionDate;

    // thong tin lien he
    @Column(name = "appt_rel_full_name")
    private String appointmentRelativesFullName;

    @Column(name = "appt_rel_phone")
    private String appointmentRelativesPhone;

    @Column(name = "appt_rel_relationship")
    private String appointmentRelativesRelationship;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "batch_detail_id")
    @JsonBackReference
    private BatchDetail batchDetail;

    @ManyToOne
    @JoinColumn(name = "vaccine_package_id")
    @JsonBackReference
    private VaccinePackage vaccinePackage;

}
