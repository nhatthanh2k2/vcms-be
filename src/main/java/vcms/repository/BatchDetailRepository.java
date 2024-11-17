package vcms.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vcms.model.BatchDetail;
import vcms.model.Vaccine;
import vcms.model.VaccineBatch;

import java.util.List;

public interface BatchDetailRepository extends JpaRepository<BatchDetail, Long> {
    List<BatchDetail> findAllByVaccineBatch(VaccineBatch vaccineBatch);

    List<BatchDetail> findAllByVaccine(Vaccine vaccine);

    BatchDetail findByVaccine(Vaccine vaccine);

    @Query("SELECT b FROM BatchDetail b WHERE b.vaccineBatch = :batch AND b.vaccine = :vaccine")
    BatchDetail findByVaccineBatchAndVaccine(@Param("batch") VaccineBatch batch, @Param("vaccine") Vaccine vaccine);

    @Query("SELECT b FROM BatchDetail b WHERE b.vaccine = :vaccine ORDER BY b.batchDetailId DESC")
    List<BatchDetail> findBatchDetailsByVaccine(@Param("vaccine") Vaccine vaccine, Pageable pageable);


}
