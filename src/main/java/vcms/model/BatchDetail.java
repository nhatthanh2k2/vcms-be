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
    @Column(name = "bd_id")
    private Long batchDetailId;

    @Column(name = "bd_rem_qty")
    private Integer batchDetailRemainingQuantity;

    @Column(name = "bd_init_qty")
    private Integer batchDetailInitialQuantity;

    @Column(name = "bd_vac_price")
    private Integer batchDetailVaccinePrice;

    @Column(name = "bd_mf_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailManufactureDate;

    @Column(name = "bd_exp_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailExpirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_batch_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;


}
