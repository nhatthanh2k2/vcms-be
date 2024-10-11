package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Customer;
import vcms.model.VaccinationRecord;

import java.util.List;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findAllByCustomer(Customer customer);
}
