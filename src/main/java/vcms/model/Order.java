package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    public Order() {
    }

    public Order(Long orderId, int orderTotal, String orderPayment,
                 LocalDate orderDate, LocalDate orderInjectionDate,
                 Customer customer, List<OrderDetail> orderDetailList) {
        this.orderId = orderId;
        this.orderTotal = orderTotal;
        this.orderPayment = orderPayment;
        this.orderDate = orderDate;
        this.orderInjectionDate = orderInjectionDate;
        this.customer = customer;
        this.orderDetailList = orderDetailList;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(int orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getOrderPayment() {
        return orderPayment;
    }

    public void setOrderPayment(String orderPayment) {
        this.orderPayment = orderPayment;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDate getOrderInjectionDate() {
        return orderInjectionDate;
    }

    public void setOrderInjectionDate(LocalDate orderInjectionDate) {
        this.orderInjectionDate = orderInjectionDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(
            List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }
}
