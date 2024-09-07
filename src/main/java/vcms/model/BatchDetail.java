package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @Column(name = "batch_detail_vaccine_quantity")
    private int batchDetailVaccineQuantity;

    @Column(name = "batch_detail_vaccine_price")
    private int batchDetailVaccinePrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccineBatch_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    @JsonBackReference
    private Vaccine vaccine;

}
