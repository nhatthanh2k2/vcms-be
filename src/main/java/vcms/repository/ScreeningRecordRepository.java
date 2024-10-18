package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.ScreeningRecord;

import java.time.LocalDate;
import java.util.List;

public interface ScreeningRecordRepository extends JpaRepository<ScreeningRecord, Long> {
    List<ScreeningRecord> findAllByScreeningRecordDate(LocalDate createDate);

    boolean existsByScreeningRecordCode(String code);
}
