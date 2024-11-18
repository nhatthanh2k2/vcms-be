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
    @Column(name = "batch_detail_id")
    private Long batchDetailId;

    @Column(name = "batch_detail__quantity")
    private Integer batchDetailVaccineQuantity;

    @Column(name = "batch_detail_total_quantity")
    private Integer batchDetailTotalVaccineQuantity;

    @Column(name = "batch_detail_vaccine_price")
    private Integer batchDetailVaccinePrice;

    @Column(name = "batch_detail_manufacture_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailManufactureDate;

    @Column(name = "batch_detail_expiration_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate batchDetailExpirationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_batch_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    private Vaccine vaccine;


}
