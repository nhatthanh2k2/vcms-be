package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.VaccineBatch;

public interface VaccineBatchRepository extends JpaRepository<VaccineBatch, Long> {

    VaccineBatch findByVaccineBatchNumber(String vaccineBatchNumber);
}
