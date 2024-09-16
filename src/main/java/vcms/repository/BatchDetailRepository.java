package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.BatchDetail;
import vcms.model.VaccineBatch;

import java.util.List;

public interface BatchDetailRepository extends JpaRepository<BatchDetail, Long> {
    List<BatchDetail> findAllByVaccineBatch(VaccineBatch vaccineBatch);


}
