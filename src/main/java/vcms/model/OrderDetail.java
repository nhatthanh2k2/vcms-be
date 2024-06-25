package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "order_details")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    @JsonBackReference
    private Vaccine vaccine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_package_id")
    @JsonBackReference
    private VaccinePackage vaccinePackage;

    public OrderDetail() {
    }

    public OrderDetail(Long orderDetailId, Order order, Vaccine vaccine,
                       VaccinePackage vaccinePackage) {
        this.orderDetailId = orderDetailId;
        this.order = order;
        this.vaccine = vaccine;
        this.vaccinePackage = vaccinePackage;
    }

    public Long getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Long orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
