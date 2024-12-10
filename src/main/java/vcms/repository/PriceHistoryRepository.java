package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.PriceHistory;
import vcms.model.Vaccine;

import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    List<PriceHistory> findAllByVaccineOrderByPriceHistoryUpdateTimeDesc(Vaccine vaccine);
}
