package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Order;
import vcms.model.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByOrder(Order order);
}
