package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.OrderDetail;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
