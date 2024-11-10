package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccineBatch_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;


}
