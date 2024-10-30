package vcms.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vcms.model.Order;

import java.time.LocalDate;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByOrderInjectionDate(LocalDate injectionDate);

    @Query("SELECT SUM(o.orderTotal) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    Long sumTotalRevenueByPeriod(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
