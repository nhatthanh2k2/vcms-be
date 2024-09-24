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
    @Column(name = "order_inj_date")
    private LocalDate orderInjectionDate;

    // Khach hang chua co ma KH thi luu thong tin
    @Column(name = "order_cust_fullname")
    private String orderCustomerFullName;

    @Column(name = "order_cust_gender")
    private Gender orderCustomerGender;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "order_cust_dob")
    private LocalDate orderCustomerDob;

    @Column(name = "order_cust_email")
    private String orderCustomerEmail;

    @Column(name = "order_cust_phone")
    private String orderCustomerPhone;

    @Column(name = "order_cust_province")
    private String orderCustomerProvince;

    @Column(name = "order_cust_district")
    private String orderCustomerDistrict;

    @Column(name = "order_cust_ward")
    private String orderCustomerWard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

}
