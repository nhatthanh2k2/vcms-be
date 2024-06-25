package vcms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vaccine_packages")
public class VaccinePackage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_package_id")
    private Long vaccinePackageId;

    @Column(name = "vaccine_package_name")
    private String vaccinePackageName;

    @Column(name = "vaccine_package_description")
    private String vaccinePackageDescription;

    @Column(name = "vaccine_package_price")
    private Double vaccinePackagePrice;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "vaccine_package_details",
            joinColumns = @JoinColumn(name = "vaccine_package_id"),
            inverseJoinColumns = @JoinColumn(name = "vaccine_id"))
    private Set<Vaccine> vaccines = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    @JsonManagedReference
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "vaccinePackage", orphanRemoval = true)
    @JsonManagedReference
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    public VaccinePackage() {
    }

    public VaccinePackage(Long vaccinePackageId, String vaccinePackageName,
                          String vaccinePackageDescription,
                          Double vaccinePackagePrice, Set<Vaccine> vaccines,
                          List<Appointment> appointmentList,
                          List<OrderDetail> orderDetailList) {
        this.vaccinePackageId = vaccinePackageId;
        this.vaccinePackageName = vaccinePackageName;
        this.vaccinePackageDescription = vaccinePackageDescription;
        this.vaccinePackagePrice = vaccinePackagePrice;
        this.vaccines = vaccines;
        this.appointmentList = appointmentList;
        this.orderDetailList = orderDetailList;
    }

    public Long getVaccinePackageId() {
        return vaccinePackageId;
    }

    public void setVaccinePackageId(Long vaccinePackageId) {
        this.vaccinePackageId = vaccinePackageId;
    }

    public String getVaccinePackageName() {
        return vaccinePackageName;
    }

    public void setVaccinePackageName(String vaccinePackageName) {
        this.vaccinePackageName = vaccinePackageName;
    }

    public String getVaccinePackageDescription() {
        return vaccinePackageDescription;
    }

    public void setVaccinePackageDescription(String vaccinePackageDescription) {
        this.vaccinePackageDescription = vaccinePackageDescription;
    }

    public Double getVaccinePackagePrice() {
        return vaccinePackagePrice;
    }

    public void setVaccinePackagePrice(Double vaccinePackagePrice) {
        this.vaccinePackagePrice = vaccinePackagePrice;
    }

    public Set<Vaccine> getVaccines() {
        return vaccines;
    }

    public void setVaccines(Set<Vaccine> vaccines) {
        this.vaccines = vaccines;
    }

    public List<Appointment> getAppointmentList() {
        return appointmentList;
    }

    public void setAppointmentList(
            List<Appointment> appointmentList) {
        this.appointmentList = appointmentList;
    }

    public List<OrderDetail> getOrderDetailList() {
        return orderDetailList;
    }

    public void setOrderDetailList(
            List<OrderDetail> orderDetailList) {
        this.orderDetailList = orderDetailList;
    }
}
