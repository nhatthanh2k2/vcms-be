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
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_total")
    private int orderTotal;

    @Column(name = "order_payment")
    private String orderPayment;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "order_date")
    private LocalDate orderDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "order_injection_date")
    private LocalDate orderInjectionDate;

    // Khach hang chua co ma KH thi luu thong tin
    @Column(name = "order_customer_name")
    private String orderCustomerFullName;

    @Column(name = "order_customer_gender")
    private Gender orderCustomerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "order_customer_dob")
    private LocalDate orderCustomerDob;

    @Column(name = "order_customer_email")
    private String orderCustomerEmail;

    @Column(name = "order_customer_phone")
    private String orderCustomerPhone;

    @Column(name = "order_customer_province")
    private String orderCustomerProvince;

    @Column(name = "order_customer_district")
    private String orderCustomerDistrict;

    @Column(name = "order_customer_ward")
    private String orderCustomerWard;

    // khach hang da co thong tin
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

}
