package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vcms.enums.Gender;

import java.time.LocalDate;
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
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_code")
    private String customerCode;

    @Column(name = "customer_name")
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
    private String customerProvince;

    @Column(name = "customer_district")
    private String customerDistrict;

    @Column(name = "customer_ward")
    private String customerWard;

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Relatives relatives;

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScreeningRecord> screeningRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<Order> orderList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL,
            mappedBy = "customer", orphanRemoval = true)
    private List<Appointment> appointmentList = new ArrayList<>();

}
