package vcms.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "batch_details")
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
    @JoinColumn(name = "order_id")
    private VaccineBatch vaccineBatch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccine_id")
    @JsonBackReference
    private Vaccine vaccine;

    public BatchDetail() {
    }

    public BatchDetail(Long batchDetailId, int batchDetailVaccineQuantity,
                       int batchDetailVaccinePrice, VaccineBatch vaccineBatch,
                       Vaccine vaccine) {
        this.batchDetailId = batchDetailId;
        this.batchDetailVaccineQuantity = batchDetailVaccineQuantity;
        this.batchDetailVaccinePrice = batchDetailVaccinePrice;
        this.vaccineBatch = vaccineBatch;
        this.vaccine = vaccine;
    }

    public Long getBatchDetailId() {
        return batchDetailId;
    }

    public void setBatchDetailId(Long batchDetailId) {
        this.batchDetailId = batchDetailId;
    }

    public int getBatchDetailVaccineQuantity() {
        return batchDetailVaccineQuantity;
    }

    public void setBatchDetailVaccineQuantity(int batchDetailVaccineQuantity) {
        this.batchDetailVaccineQuantity = batchDetailVaccineQuantity;
    }

    public int getBatchDetailVaccinePrice() {
        return batchDetailVaccinePrice;
    }

    public void setBatchDetailVaccinePrice(int batchDetailVaccinePrice) {
        this.batchDetailVaccinePrice = batchDetailVaccinePrice;
    }

    public VaccineBatch getVaccineBatch() {
        return vaccineBatch;
    }

    public void setVaccineBatch(VaccineBatch vaccineBatch) {
        this.vaccineBatch = vaccineBatch;
    }

    public Vaccine getVaccine() {
        return vaccine;
    }

    public void setVaccine(Vaccine vaccine) {
        this.vaccine = vaccine;
    }
}
