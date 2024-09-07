package vcms.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "vaccine_packages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

}
