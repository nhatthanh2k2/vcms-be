package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq",
            allocationSize = 1, initialValue = 1000)
    @Column(name = "cust_id")
    private Long customerId;

    @Column(name = "cust_code")
    private String customerCode;

    @Column(name = "cust_full_name")
    private String customerFullName;

    @Column(name = "cust_gender")
    private Gender customerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "cust_dob")
    private LocalDate customerDob;

    @Column(name = "cust_email")
    private String customerEmail;

    @Column(name = "cust_phone")
    private String customerPhone;

    @Column(name = "cust_province")
    private String customerProvince;

    @Column(name = "cust_district")
    private String customerDistrict;

    @Column(name = "cust_ward")
    private String customerWard;

    @Column(name = "cust_createAt")
    @JsonFormat(pattern = "dd-MM-yyyy HH-mm-ss")
    private LocalDateTime customerCreateAt;

    @Column(name = "cust_updateAt")
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

}
