package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vcms.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderInjectionDate(LocalDate injectionDate);
}
