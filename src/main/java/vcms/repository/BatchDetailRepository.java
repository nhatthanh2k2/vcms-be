package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.BatchDetail;

public interface BatchDetailRepository extends JpaRepository<BatchDetail, Long> {
}
