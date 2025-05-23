package vcms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
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
    @Column(name = "vb_id")
    private Long vaccineBatchId;

    @Column(name = "vb_number")
    private String vaccineBatchNumber;

    @Column(name = "vb_imp_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate vaccineBatchImportDate;

    @Column(name = "vb_quantity")
    private Integer vaccineBatchQuantity;

    @Column(name = "vb_value")
    private BigInteger vaccineBatchValue;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vaccineBatch",
            orphanRemoval = true)
    private List<VaccinationRecord> vaccinationRecordList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vaccineBatch",
            orphanRemoval = true)
    private List<BatchDetail> batchDetailList = new ArrayList<>();

}
