package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
