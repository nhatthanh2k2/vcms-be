package vcms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vaccine_batches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccineBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_batch_id")
    private Long vaccineBatchId;

    @Column(name = "vaccine_batch_number")
    private String vaccineBatchNumber;

    @Column(name = "vaccine_batch_import_date")
    private LocalDate vaccineBatchImportDate;

    @Column(name = "vaccine_batch_quantity")
    private int vaccineBatchQuantity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vaccineBatch",
            orphanRemoval = true)
    private List<BatchDetail> batchDetailList = new ArrayList<>();

}
