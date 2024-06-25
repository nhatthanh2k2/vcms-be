package vcms.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "vaccine_batches")
public class VaccineBatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccine_batch_id")
    private Long vaccineBatchId;

    @Column(name = "vaccine_batch_number")
    private String vaccineBatchNumber;

    @Column(name = "vaccine_batch_import_date")
    private LocalDate vaccineBatchImportDate;

    @Column(name = "vaccine_batch_expiry_date")
    private LocalDate vaccineBatchExpiryDate;

    @Column(name = "vaccine_batch_quantity")
    private int vaccineBatchQuantity;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "vaccineBatch",
            orphanRemoval = true)
    private List<BatchDetail> batchDetailList = new ArrayList<>();

    public VaccineBatch() {
    }

    public VaccineBatch(Long vaccineBatchId, String vaccineBatchNumber,
                        LocalDate vaccineBatchImportDate,
                        LocalDate vaccineBatchExpiryDate,
                        int vaccineBatchQuantity,
                        List<BatchDetail> batchDetailList) {
        this.vaccineBatchId = vaccineBatchId;
        this.vaccineBatchNumber = vaccineBatchNumber;
        this.vaccineBatchImportDate = vaccineBatchImportDate;
        this.vaccineBatchExpiryDate = vaccineBatchExpiryDate;
        this.vaccineBatchQuantity = vaccineBatchQuantity;
        this.batchDetailList = batchDetailList;
    }

    public Long getVaccineBatchId() {
        return vaccineBatchId;
    }

    public void setVaccineBatchId(Long vaccineBatchId) {
        this.vaccineBatchId = vaccineBatchId;
    }

    public String getVaccineBatchNumber() {
        return vaccineBatchNumber;
    }

    public void setVaccineBatchNumber(String vaccineBatchNumber) {
        this.vaccineBatchNumber = vaccineBatchNumber;
    }

    public LocalDate getVaccineBatchImportDate() {
        return vaccineBatchImportDate;
    }

    public void setVaccineBatchImportDate(LocalDate vaccineBatchImportDate) {
        this.vaccineBatchImportDate = vaccineBatchImportDate;
    }

    public LocalDate getVaccineBatchExpiryDate() {
        return vaccineBatchExpiryDate;
    }

    public void setVaccineBatchExpiryDate(LocalDate vaccineBatchExpiryDate) {
        this.vaccineBatchExpiryDate = vaccineBatchExpiryDate;
    }

    public int getVaccineBatchQuantity() {
        return vaccineBatchQuantity;
    }

    public void setVaccineBatchQuantity(int vaccineBatchQuantity) {
        this.vaccineBatchQuantity = vaccineBatchQuantity;
    }

    public List<BatchDetail> getBatchDetailList() {
        return batchDetailList;
    }

    public void setBatchDetailList(
            List<BatchDetail> batchDetailList) {
        this.batchDetailList = batchDetailList;
    }
}
