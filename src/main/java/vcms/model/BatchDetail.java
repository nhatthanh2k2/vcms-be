package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "batch_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BatchDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_det_id")
    private Long batchDetailId;

    @Column(name = "batch_det_vac_qty")
    private int batchDetailVaccineQuantity;

    @Column(name = "batch_det_vac_price")
    private int batchDetailVaccinePrice;

    @Column(name = "batch_det_manuf_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailManufactureDate;

    @Column(name = "batch_det_exp_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailExpirationDate;

    @Column(name = "batch_det_vac_type", length = 2000)
    private String vaccineType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccineBatch_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    @JsonIgnore
    private Vaccine vaccine;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "batchDetail", orphanRemoval = true)
    @JsonIgnore
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "batchDetail", orphanRemoval = true)
    @JsonIgnore
    private List<Appointment> appointmentList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY,
            mappedBy = "batchDetail", orphanRemoval = true)
    @JsonIgnore
    private List<OrderDetail> orderDetailList = new ArrayList<>();

}
