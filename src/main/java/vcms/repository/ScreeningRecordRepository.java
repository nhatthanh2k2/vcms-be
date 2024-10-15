package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.ScreeningRecord;

public interface ScreeningRecordRepository extends JpaRepository<ScreeningRecord, Long> {
}
